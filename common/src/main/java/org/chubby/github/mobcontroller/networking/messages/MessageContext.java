package org.chubby.github.mobcontroller.networking.messages;

public interface MessageContext {
    void execute(Runnable task);
    void setHandled(boolean handled);
    boolean isClientSide();
    boolean isServerSide();
}