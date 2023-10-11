/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package diabetescalculator.Model;

import java.util.Map;

/**
 *
 * @author OscarFabianHP
 */
/*
Enum que contiene los datos de los valores a aumentar o disminuir en la tasa basal según la TDD y la subida o caida de glucosa presentada en una prueba de dosis basal 
*/
public enum ChooseCheckChangeBasalRatesEnum {
    //enum of u/hr that basal rate low or raise glucose 
    TDD_20U(20, Map.ofEntries(Map.entry(20, "0.025"), Map.entry(30, "0.025"), 
            Map.entry(40, "0.025"), Map.entry(50, "0.025" ), Map.entry(60, "0.075"), 
            Map.entry(70, "0.075"), Map.entry(80, "0.075") )),
    TDD_30U(30, Map.ofEntries( Map.entry(20, "0.05"), Map.entry(30, "0.05"), 
            Map.entry(40, "0.05"), Map.entry(50, "0.05"), Map.entry(60, "0.125"), 
            Map.entry(70, "0.125"), Map.entry(80, "0.125") )),
    TDD_40U(40, Map.ofEntries( Map.entry(20, "0.075"), Map.entry(30, "0.075"), 
            Map.entry(40, "0.075"), Map.entry(50, "0.075"), Map.entry(60, "0.15"), 
            Map.entry(70, "0.15"), Map.entry(80, "0.15") )),
    TDD_50U(50, Map.ofEntries( Map.entry(20, "0.1"), Map.entry(30, "0.1"), 
            Map.entry(40, "0.1"), Map.entry(50, "0.1"), Map.entry(60, "0.225"), 
            Map.entry(70, "0.225"), Map.entry(80, "0.225") )),
    TDD_60U(60, Map.ofEntries(Map.entry(20, "0.125"), Map.entry(30, "0.125"), 
            Map.entry(40, "0.125"), Map.entry(50, "0.125"), Map.entry(60, "0.25"), 
            Map.entry(70, "0.25"), Map.entry(80, "0.25") )),
    TDD_80U(80, Map.ofEntries( Map.entry(20, "0.15"), Map.entry(30, "0.15"), 
            Map.entry(40, "0.15"), Map.entry(50, "0.15"), Map.entry(60, "0.325"), 
            Map.entry(70, "0.325"), Map.entry(80, "0.325") )),
    TDD_100U(100, Map.ofEntries( Map.entry(20, "0.2"), Map.entry(30, "0.2"), 
            Map.entry(40, "0.2"), Map.entry(50, "0.2"), Map.entry(60, "0.4"), 
            Map.entry(70, "0.4"), Map.entry(80, "0.4") ));
    
    private final Integer tdd;
    private final Map<Integer, String> lowerOrRaiseBasal;
    
    private ChooseCheckChangeBasalRatesEnum(Integer tdd, Map<Integer, String> lowerOrRaiseBasal) {
        this.tdd = tdd;
        this.lowerOrRaiseBasal = lowerOrRaiseBasal;
    }

    public Integer getTdd() {
        return tdd;
    }
    public Map<Integer, String> getLowerOrRaiseBasal() {
        return lowerOrRaiseBasal;
    }
}