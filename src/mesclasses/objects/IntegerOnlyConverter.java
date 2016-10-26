/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.objects;

import javafx.util.converter.NumberStringConverter;
import mesclasses.util.NodeUtil;

/**
 *
 * @author rrrt3491
 */
public class IntegerOnlyConverter extends NumberStringConverter {
    
    @Override public Number fromString(String value) {
        return super.fromString(NodeUtil.makeIntegerOnly(value));
    }
}
