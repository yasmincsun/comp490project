/**
 * Class Name: Address
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 * Description:
 * This class represents address information for a venue or event location.
 * It stores up to three address lines returned from the API.
 */
package com.musicApp.backend.map.TicketMaster.Address;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address {
    String line1;
    String line2;
    String line3;

    /**
     * Returns the first line of the address.
     *
     * @return the first address line
     */
    public String getLine1() {
        return line1;
    }

    /**
     * Returns the second line of the address.
     *
     * @return the second address line
     */
    public String getLine2() {
        return line2;
    }

    /**
     * Returns the third line of the address.
     *
     * @return the third address line
     */
    public String getLine3() {
        return line3;
    }
}