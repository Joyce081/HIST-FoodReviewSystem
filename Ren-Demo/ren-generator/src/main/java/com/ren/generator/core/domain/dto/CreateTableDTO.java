package com.ren.generator.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * CreateTableDTO
 *
 * @author ren
 * @version 2025/08/10 16:54
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CreateTableDTO {

	String sqlStr;

}
