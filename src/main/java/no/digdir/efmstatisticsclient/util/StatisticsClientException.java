package no.digdir.efmstatisticsclient.util;

public class StatisticsClientException extends RuntimeException{

    public StatisticsClientException(String message) { super(message); }

    public StatisticsClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
//TODO slett om ikkje i bruk