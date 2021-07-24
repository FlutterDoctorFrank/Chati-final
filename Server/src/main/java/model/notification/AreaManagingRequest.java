package model.notification;

import model.MessageBundle;
import model.context.Context;
import model.context.spatial.SpatialContext;
import model.user.User;

public class AreaManagingRequest extends Notification {

    public AreaManagingRequest(User owner, Context owningContext, String userMessage, User requestingUser, SpatialContext requestedContext) {
        super(owner, owningContext, new MessageBundle("areManagingKey", new Object[]{userMessage}), requestingUser, requestedContext);
    }

    @Override
    public void accept() {
        // TODO
    }

    @Override
    public void decline() {
        // TODO
    }
}
