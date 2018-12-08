package ro.code4.monitorizarevot.data.chat.socket;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ro.code4.monitorizarevot.BuildConfig;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

@Singleton
public class SocketConnector {

    private static final String CHAT_HISTORY = "chat_history";

    private static final String NEW_MESSAGE = "new_message";

    private final List<String> mEventNames = Arrays.asList(Socket.EVENT_CONNECT, Socket.EVENT_CONNECT_ERROR, Socket.EVENT_DISCONNECT,
                                                           CHAT_HISTORY, NEW_MESSAGE);

    private Socket mSocket;

    private Observable<SocketEvent> mSocketEventObservable = Observable.create(new Observable.OnSubscribe<SocketEvent>() {

        @Override
        public void call(final Subscriber<? super SocketEvent> subscriber) {
            for (final String eventName : mEventNames) {
                mSocket.on(eventName, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(new SocketEvent(args, eventName));
                        }
                    }
                });
            }

            mSocket.connect();
        }
    });

    private Observable<SocketEvent> mChatHistoryObservable = mSocketEventObservable.filter(new Func1<SocketEvent, Boolean>() {

        @Override
        public Boolean call(SocketEvent socketEvent) {
            return socketEvent.getEvent().equals(CHAT_HISTORY);
        }
    });

    private Observable<SocketEvent> mNewMessageObservable = mSocketEventObservable.filter(new Func1<SocketEvent, Boolean>() {

        @Override
        public Boolean call(SocketEvent socketEvent) {
            return socketEvent.getEvent().equals(NEW_MESSAGE) && socketEvent.getArguments() != null && socketEvent.getArguments().length > 0;
        }
    });

    @Inject
    public SocketConnector() {
        initSocket();
    }

    private void initSocket() {
        try {
            mSocket = IO.socket(BuildConfig.CHAT_SOCKET_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Observable<SocketEvent> history() {
        return mChatHistoryObservable;
    }

    public Observable<SocketEvent> newMessage() {
        return mNewMessageObservable;
    }

    public void sendMessage(String message) {
        if (mSocket != null && mSocket.connected()) {
            mSocket.emit(NEW_MESSAGE, message);
        }
    }

    public void disconnect() {
        mSocket.disconnect();
        for (final String eventName : mEventNames) {
            mSocket.off(eventName);
        }
    }
}
