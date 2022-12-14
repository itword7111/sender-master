package org.dobrodey.sender.saop;

import com.sun.xml.ws.fault.ServerSOAPFaultException;
import org.dobrodey.generate.IOException_Exception;
import org.dobrodey.generate.RouterSenderService;
import org.dobrodey.generate.RouterSenderServiceImplService;
import org.dobrodey.sender.model.Report;
import org.dobrodey.sender.model.Role;

import javax.xml.datatype.XMLGregorianCalendar;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.dobrodey.sender.Properties.REPORT_URL;

public class SenderRouterService {

    private final URL url = new URL(REPORT_URL+"?wsdl");
    private RouterSenderService hello;

    public SenderRouterService() throws MalformedURLException {
    }

    public void init() {
        RouterSenderServiceImplService service = new RouterSenderServiceImplService(url);
        this.hello = service.getRouterSenderServiceImplPort();
    }

    public List<Report> getReportsToday() {
        return hello.getReportsToday()
                .getItem()
                .stream()
                .map(report -> Report.builder()
                        .nickName(report.getUserName())
                        .task(report.getTask())
                        .timeOfTrack(getTimeOfTrack(report.getTimeOfTrack()))
                        .build())
                .collect(Collectors.toList());
    }

    private static LocalDateTime getTimeOfTrack(XMLGregorianCalendar time) {
        return LocalDateTime.of(time.getYear(),
                time.getMonth(),
                time.getDay(),
                time.getHour(),
                time.getMinute());
    }

    public void pdfForLector(byte[] pdfBytes) throws IOException_Exception {
        try {
            hello.pdf(pdfBytes, hello.getUserNamesByRole(Role.lector.name()));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
