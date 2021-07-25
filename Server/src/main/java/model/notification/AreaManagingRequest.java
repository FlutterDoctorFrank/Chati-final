package model.notification;

import model.MessageBundle;
import model.context.Context;
import model.context.spatial.SpatialContext;
import model.user.User;

public class AreaManagingRequest extends Notification {
    // TODO

    public AreaManagingRequest(User owner, Context owningContext, MessageBundle messageBundle) {
        super(owner, owningContext, messageBundle);
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
