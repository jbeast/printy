package uk.ac.sanger.sprint.service.protocol;

import org.springframework.stereotype.Component;
import uk.ac.sanger.sprint.model.Printer;
import uk.ac.sanger.sprint.model.Protocol;

@Component
public class PrintProtocolAdapterFactoryImplementation implements PrintProtocolAdapterFactory {
    private final FTPStoreFactory ftpStoreFactory;

    public PrintProtocolAdapterFactoryImplementation(FTPStoreFactory ftpStoreFactory) {
        this.ftpStoreFactory = ftpStoreFactory;
    }

    @Override
    public PrintProtocolAdapter getPrintProtocolAdapter(Printer printer) {
        Protocol protocol = printer.getPrinterType().getProtocol();
        if (protocol == Protocol.FTP) {
            return new FtpPrintProtocolAdapter(printer, ftpStoreFactory);
        }
        throw new UnsupportedOperationException("Unsupported printing protocol: "+protocol);
    }
}
