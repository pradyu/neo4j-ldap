package com.pradyu.Util;

import com.google.common.base.Preconditions;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

@Service
public class ObjectGuidConverter {

    private static final String DASH = "-";

    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    /**
     * Converts a 32-character hex string that representing an Active Directory objectGUID
     * (using Windows-native byte ordering) into a canonical 36-character GUID (UUID format).
     */
    public String nativeToGuid(@Nonnull String s) {
        Preconditions.checkNotNull(s, "The parameter must not be null.");
        Preconditions.checkArgument(s.length() == 32, "The parameter must be of length 32.");
        return hyphenate(reverseWindowsNativeIntegerEndianess(s));
    }

    /**
     * Converts a canonical 36-character GUID (UUID format) to a 32-character hex string
     * that represents an Active Directory objectGUID (using Windows-native byte ordering).
     */
    public String guidToNative(@Nonnull String s) {
        Preconditions.checkNotNull(s, "The parameter must not be null.");
        Preconditions.checkArgument(s.length() == 36, "The parameter must be of length 36.");
        return reverseWindowsNativeIntegerEndianess(dehyphenate(s));
    }

    /**
     * The first three (of five) components of a Windows objectGUID
     * are represented Natively in Windows as little-endian integers.
     * This method takes a 32-character hex string (16 bytes) and returns
     * a string of the same length, but reversing the order of bytes 0-4, 5-6, and 7-8,
     * while leaving the remaining 16 bytes intact.
     * <p/>
     * See "The .NET developer's guide to directory services programming" By Joe Kaplan, Ryan Dunn, Chapter 3, p. 74
     * http://www.amazon.com/Developers-Guide-Directory-Services-Programming/dp/0321350170
     * http://books.google.com/books?id=kGApqjobEfsC&printsec=frontcover&dq=The+.NET+Developer%27s+Guide+to+Directory+Services+Programming&source=bl&ots=p5moXaUNO8&sig=sVNF57OfXD05S4UzDqF9rYBp7kI&hl=en&ei=bJiTTYmQMY7GsAPzvcHcBQ&sa=X&oi=book_result&ct=result&resnum=5&ved=0CDgQ6AEwBA#v=onepage&q=objectGUID&f=false
     * http://directoryprogramming.net/
     * http://www.eggheadcafe.com/software/aspnet/32863987/different-guid-formats.aspx
     */
    protected String reverseWindowsNativeIntegerEndianess(@Nonnull String s) {
        Preconditions.checkNotNull(s, "The parameter must not be null.");
        Preconditions.checkArgument(s.length() == 32, "The parameter must be of length 32.");
        Preconditions.checkArgument(!s.contains(DASH), "The parameter must not contain hyphens.");

        StringBuffer sb = new StringBuffer();

        sb.append(s.substring(6, 8)); // Reverse order of 1st-4th bytes
        sb.append(s.substring(4, 6));
        sb.append(s.substring(2, 4));
        sb.append(s.substring(0, 2));

        sb.append(s.substring(10, 12)); // Reverse order of 5th and 6th byte
        sb.append(s.substring(8, 10));

        sb.append(s.substring(14, 16)); // Reverse order of 7th and 8th byte
        sb.append(s.substring(12, 14));

        sb.append(s.substring(16, 32)); // Order of bytes 9-16 remains intact

        return sb.toString();
    }

    /**
     * Adds hyphens to a 32-character hex string so as to conform with
     * standard formating of a UUID
     * This method expects a string with no hyphens.
     */
    protected String hyphenate(@Nonnull String s) {
        Preconditions.checkNotNull(s, "The parameter must not be null.");
        Preconditions.checkArgument(s.length() == 32, "The parameter must be of length 32.");
        Preconditions.checkArgument(!s.contains(DASH), "The parameter must not contain hyphens.");
        return s.substring(0, 8) + DASH
                + s.substring(8, 12) + DASH
                + s.substring(12, 16) + DASH
                + s.substring(16, 20) + DASH
                + s.substring(20, 32);
    }

    /**
     * Returns a version of the input string with all hyphens removed.
     * This method expects a standard UUID formatted string (32 hex chars and 4 hyphens).
     */
    protected String dehyphenate(@Nonnull String s) {
        Preconditions.checkNotNull(s, "The parameter must not be null.");
        Preconditions.checkArgument(s.length() == 36, "The parameter must be of length 36.");
        Preconditions.checkArgument(DASH.equals(s.substring(8, 9)));
        Preconditions.checkArgument(DASH.equals(s.substring(13, 14)));
        Preconditions.checkArgument(DASH.equals(s.substring(18, 19)));
        Preconditions.checkArgument(DASH.equals(s.substring(23, 24)));
        return s.replaceAll(DASH, "");
    }

    /**
     * Converts a byte[] into a hex string (two characters for each byte).
     */
    public String byteArrayToHexString(byte[] bytes) {
        char[] chars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            chars[i * 2] = HEX_CHARS[(bytes[i] & 0xF0) >>> 4];
            chars[i * 2 + 1] = HEX_CHARS[bytes[i] & 0x0F];
        }
        return new String(chars);
    }

}
