/**
 * Class Name: Embedded
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 */
package com.musicApp.backend.map.EmbeddedData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents embedded venue data returned from the API.
 * It is used to store an array of venue objects associated with an event.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Embedded {
    private Venues[] venues;
}
