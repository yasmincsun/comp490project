/**
 * Class Name: PagesDTO
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 * Description:
 * This class represents page-related data returned from the API. It is used
 * as a data transfer object to store the total number of pages available.
 */
package com.musicApp.backend.map.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PagesDTO {
    private int totalPages;
}