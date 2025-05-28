package ua.gorobeos.contextor.context.storage.context_full_load.conditional.file;

import ua.gorobeos.contextor.context.annotations.Element;
import ua.gorobeos.contextor.context.annotations.conditions.ConditionalOnFilePresence;

@Element
@ConditionalOnFilePresence(filePaths = {"non-existing-file.txt"})
public class FirstOnFileCondition {

}
