package net.script.logic.settings.summarizer;

import lombok.Data;
import net.script.logic.settings.SimpleLinguisticVariableSetting;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Data
@Root(name = "summarizers")
public class SummarizersSettings {
    @ElementList(name = "definitions", entry = "summarizer")
    private List<SimpleLinguisticVariableSetting> summarizers;
}
