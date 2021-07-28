package model.Context;

import model.Communication.CommunicationRegion;
import model.Context.Global.GlobalContext;
import model.Context.Spatial.SpatialContext;
import model.communication.CommunicationMedium;
import model.context.ContextID;
import model.context.spatial.Music;

import java.util.Map;
import java.util.Set;

/**
 * Eine Klasse, welche einen Kontext der Anwendung repräsentiert. Ein Kontext legt den
 * Umfang fest, in welchem Benutzer Benachrichtigungen empfangen können, Rollen haben
 * können, kommunizieren können und sonstige Benutzeraktionen durchführen können.
 */
public abstract class Context implements IContextView{

    private String contextName;
    protected Map<ContextID, SpatialContext> children;
    private Context parent;
    private ContextID contextId;
    private CommunicationRegion communicationRegion;
    private Set<CommunicationMedium> communicationMedia;
    private Music music;


    public Context(String contextName, Context parent, ContextID contextId) {
        this.contextName = contextName;
        this.parent = parent;
        this.contextId = contextId;
    }


    @Override
    public ContextID getContextId() {
        return contextId;
    }

    @Override
    public String getContextName() {
        return contextName;
    }

    @Override
    public Music getMusic() {
        return music;
    }

    @Override
    public CommunicationRegion getCommunicationRegion() {
        return communicationRegion;
    }

    @Override
    public Set<CommunicationMedium> getCommunicationMedia() {
        return communicationMedia;
    }


    /**
     * Prüft, ob dieser Kontext in einem anderen Kontext enthalten ist.
     * @param context: Zu überprüfender Kontext.
     * @return Gibt truezurück, wenn der Kontext im übergebenen Kontext enthalten
     * ist, sonst false.
     */
    public boolean isInContext(Context context){
        if(this.equals(GlobalContext.getInstance())){
            return this.equals(context) ? true : false;
        }
        return this.equals(context) ? parent.isInContext(context) : true;
    }

    /**
     * gibt den Context einer contextId zurück, wenn sich dieser im Unterbaum des aktuellen Kontextes befindet
     * @param contextId Id des gesuchten Kontextes
     * @return Context, wenn im Unterbaum enthalten, ansonsten null
     */
    public Context getContext(ContextID contextId) {
        Context found = null;
        if (getContextId().equals(contextId)) {
            found = this;
        } else {
            for (Map.Entry<ContextID, SpatialContext> entry : children.entrySet()) {
                Context child = entry.getValue();
                found = child.getContext(contextId);
                if (found != null) {
                    break;
                }
            }
        }
        return found;
    }

    public void setMusic(Music music) {
        this.music = music;
    }
}
