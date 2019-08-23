package poc.facet.core.listeners;


import org.jetbrains.annotations.Contract;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;

import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import java.util.Arrays;
import java.util.List;

import static org.nuxeo.ecm.core.api.event.DocumentEventTypes.*;
import static poc.facet.core.constants.POCConstants.ALL_DOMAINS;

public class AddFacetBeforeCreation implements EventListener {
  

    @Override
    public void handleEvent(Event event) {
        EventContext ctx = event.getContext();
        if (!(ctx instanceof DocumentEventContext)) {
          return;
        }

        DocumentEventContext docCtx = (DocumentEventContext) ctx;
        DocumentModel doc = docCtx.getSourceDocument();

        String eventName = event.getName();

        if (eventName.equals(EMPTY_DOCUMENTMODEL_CREATED) && !doc.isProxy() && !doc.isImmutable()){

            List<String> typeList = Arrays.asList("File", "Picture", "Video", "Note", "Audio", "PublishingAsset");
            if (typeList.contains(doc.getType())) {
                String[] pathParts = ctx.getProperty("parentPath").toString().split("/", 4);
                String targetDomain = pathParts[2];
                if (checkDomain(targetDomain)) {
                    if (!doc.hasFacet(targetDomain)) doc.addFacet(targetDomain);
                }
                if ("PublishingAsset".equals(doc.getType())) {
                    doc.setPropertyValue("publishingasset:g_controllingBusiness", targetDomain);
                }
            }
        }
    }

    private static boolean checkDomain(String targetValue) {
        for(String s: ALL_DOMAINS){
            if(s.equals(targetValue))
                return true;
        }
        return false;
    }
}
