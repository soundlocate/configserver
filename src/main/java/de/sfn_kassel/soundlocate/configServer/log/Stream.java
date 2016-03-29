package de.sfn_kassel.soundlocate.configServer.log;

/**
 * Created by jaro on 29.03.16.
 * the Enum used for representing the two streams in the Logger
 */
public enum Stream {
    STD_OUT, STD_ERR;

    @Override
    public String toString() {
        return this == STD_OUT ? "o" : "e";
    }
}