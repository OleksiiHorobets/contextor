package ua.gorobeos.contextor.context.storage.context_full_load.conditional.web;

import ua.gorobeos.contextor.context.annotations.Element;
import ua.gorobeos.contextor.context.annotations.conditions.ConditionalOnWebRequest;
import ua.gorobeos.contextor.context.annotations.conditions.ConditionalOnWebRequest.HttpMethod;

@Element
@ConditionalOnWebRequest(
    method = HttpMethod.GET,
    url = "https://nonexistingsite--+++.sdsf/"
)
public class FailedWebDependentElement {

}
