package com.ecaservice.data.storage.report;

import com.ecaservice.data.storage.model.report.ReportTypeVisitor;
import eca.data.AbstractDataSaver;
import eca.data.file.arff.ArffFileSaver;
import eca.data.file.csv.CsvSaver;
import eca.data.file.json.JsonSaver;
import eca.data.file.text.DATASaver;
import eca.data.file.text.DocxSaver;
import eca.data.file.xls.XLSSaver;
import eca.data.file.xml.XmlSaver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Class for providing report saver based on report type.
 *
 * @author Roman Batygin
 */
@Component
@RequiredArgsConstructor
public class ReportProvider implements ReportTypeVisitor<AbstractDataSaver> {

    @Override
    public AbstractDataSaver visitXls() {
        return new XLSSaver();
    }

    @Override
    public AbstractDataSaver visitCsv() {
        return new CsvSaver();
    }

    @Override
    public AbstractDataSaver visitArff() {
        return new ArffFileSaver();
    }

    @Override
    public AbstractDataSaver visitJson() {
        return new JsonSaver();
    }

    @Override
    public AbstractDataSaver visitXml() {
        return new XmlSaver();
    }

    @Override
    public AbstractDataSaver visitTxt() {
        return new DATASaver();
    }

    @Override
    public AbstractDataSaver visitData() {
        return new DATASaver();
    }

    @Override
    public AbstractDataSaver visitDocx() {
        return new DocxSaver();
    }
}
