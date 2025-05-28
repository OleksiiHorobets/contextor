package ua.gorobeos.contextor.context.storage.context_full_load.conditional.file;

import ua.gorobeos.contextor.context.annotations.Element;
import ua.gorobeos.contextor.context.annotations.conditions.ConditionalOnFilePresence;

@Element
@ConditionalOnFilePresence(filePaths = {"file-test/second.properties"})
public class SecondOnFileCondition implements FileConditional {

}
