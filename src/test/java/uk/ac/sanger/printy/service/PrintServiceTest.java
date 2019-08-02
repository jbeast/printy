package uk.ac.sanger.printy.service;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.sanger.printy.model.*;
import uk.ac.sanger.printy.service.language.PrinterLanguageAdapter;
import uk.ac.sanger.printy.service.language.PrinterLanguageAdapterFactory;
import uk.ac.sanger.printy.service.protocol.PrintProtocolAdapter;
import uk.ac.sanger.printy.service.protocol.PrintProtocolAdapterFactory;
import uk.ac.sanger.printy.service.status.StatusProtocolAdapter;
import uk.ac.sanger.printy.service.status.StatusProtocolAdapterFactory;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;

/**
 * Tests for {@link PrintService}
 * @author dr6
 */
@Test
public class PrintServiceTest {
    @Mock
    private PrinterLanguageAdapterFactory mockLanguageAdapterFactory;
    @Mock
    private PrintProtocolAdapterFactory mockProtocolAdapterFactory;
    @Mock
    private StatusProtocolAdapterFactory mockStatusAdapterFactory;

    @Mock
    private PrinterLanguageAdapter mockLanguageAdapter;
    @Mock
    private PrintProtocolAdapter mockProtocolAdapter;
    @Mock
    private StatusProtocolAdapter mockStatusAdapter;

    @Mock
    private PrintRequest mockPrintRequest;
    @Mock
    private Printer mockPrinter;

    private PrintService service;

    private final String printCode = "MyPrintCode";

    @BeforeMethod
    private void setupMocks() {
        initMocks(this);
        when(mockLanguageAdapterFactory.getLanguageAdapter(any())).thenReturn(mockLanguageAdapter);
        when(mockProtocolAdapterFactory.getPrintProtocolAdapter(any())).thenReturn(mockProtocolAdapter);
        when(mockStatusAdapterFactory.getStatusProtocolAdapter(any())).thenReturn(mockStatusAdapter);

        when(mockLanguageAdapter.transcribe(any(), any())).thenReturn(printCode);

        service = new PrintServiceImplementation(mockLanguageAdapterFactory, mockProtocolAdapterFactory, mockStatusAdapterFactory);
    }

    private void mockPrinter(StatusProtocol statusProtocol) {
        PrinterType printerType = new PrinterType("tt", PrinterLanguage.JSCRIPT, Protocol.FTP, statusProtocol, null);
        when(mockPrinter.getPrinterType()).thenReturn(printerType);
        when(mockPrinter.getHostname()).thenReturn("printerhost");
    }

    @Test
    public void testPrintWithStatusProtocol() throws IOException {
        testPrint(StatusProtocol.SOAP);
    }

    @Test
    public void testPrintWithNoStatusProtocol() throws IOException {
        testPrint(null);
    }

    private void testPrint(StatusProtocol statusProtocol) throws IOException {
        mockPrinter(statusProtocol);

        String result = service.print(mockPrintRequest, mockPrinter);

        ArgumentCaptor<String> jobIdCaptor = ArgumentCaptor.forClass(String.class);

        verify(mockLanguageAdapterFactory).getLanguageAdapter(mockPrinter);
        verify(mockLanguageAdapter).transcribe(eq(mockPrintRequest), jobIdCaptor.capture());
        String jobId = jobIdCaptor.getValue();
        assertNotNull(jobId);
        assertFalse(jobId.isEmpty());
        verify(mockProtocolAdapterFactory).getPrintProtocolAdapter(mockPrinter);
        verify(mockProtocolAdapter).print(printCode);

        if (statusProtocol==null) {
            assertNull(result);
        } else {
            assertEquals(result, "printerhost:"+jobId);
        }
    }

    @Test
    public void testGetPrintStatus() throws IOException {
        mockPrinter(StatusProtocol.SOAP);
        PrintStatus printStatus = new PrintStatus(5, 7);
        when(mockStatusAdapter.getPrintStatus(any())).thenReturn(printStatus);
        PrintStatus result = service.getPrintStatus(mockPrinter, "myjobid");
        verify(mockStatusAdapterFactory).getStatusProtocolAdapter(mockPrinter);
        verify(mockStatusAdapter).getPrintStatus("myjobid");

        assertEquals(result, printStatus);
    }
}
