package com.github.frapontillo.pulse.crowd.lemmatize;

import com.github.frapontillo.pulse.crowd.data.entity.Message;
import com.github.frapontillo.pulse.crowd.data.entity.Token;
import com.github.frapontillo.pulse.rx.PulseSubscriber;
import com.github.frapontillo.pulse.spi.IPlugin;
import rx.Observable;
import rx.Subscriber;

import java.util.List;

/**
 * {@link rx.Observable.Operator} implementation that lemmatizes incoming {@link Message}s by
 * delegating the process to the {@link #lemmatizeMessageTokens(Message)} method that lemmatizers
 * should implement.
 *
 * @author Francesco Pontillo
 */
public abstract class ILemmatizerOperator implements Observable.Operator<Message, Message> {
    private IPlugin plugin;

    public ILemmatizerOperator(IPlugin plugin) {
        this.plugin = plugin;
    }

    @Override public Subscriber<? super Message> call(Subscriber<? super Message> subscriber) {
        return new PulseSubscriber<Message>(subscriber) {
            @Override public void onNext(Message message) {
                plugin.reportElementAsStarted(message.getId());
                message = lemmatizeMessage(message);
                plugin.reportElementAsEnded(message.getId());
                subscriber.onNext(message);
            }

            @Override public void onCompleted() {
                plugin.reportPluginAsCompleted();
                super.onCompleted();
            }

            @Override public void onError(Throwable e) {
                plugin.reportPluginAsErrored();
                super.onError(e);
            }
        };
    }

    /**
     * Lemmatize all the {@link Token}s of the {@link Message}, then return the input {@link
     * Message}.
     *
     * @param message The {@link Message} to process.
     *
     * @return the {@link Message} with lemmatized {@link Token}s.
     */
    public Message lemmatizeMessage(Message message) {
        message.setTokens(lemmatizeMessageTokens(message));
        return message;
    }

    /**
     * Lemmatize the given {@link Token}s by setting the {@link Token#lemma} property.
     *
     * @param message The {@link Message} whose {@link List} of {@link Token}s will be modified in
     *                order to include the (optional) lemma.
     *
     * @return The same {@link List} of {@link Token}s of the input {@link Message}, eventually
     * modified.
     */
    public abstract List<Token> lemmatizeMessageTokens(Message message);

}
