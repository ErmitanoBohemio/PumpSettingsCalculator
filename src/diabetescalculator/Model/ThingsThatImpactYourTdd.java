/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package diabetescalculator.Model;

import java.util.List;
import java.util.Map;

/**
 *
 * @author OscarFabianHP
 */
/*
Tabla que muestra la clasificación  según el peso y el tdd actual, Asi:
Below are typical TDDs for people who weigh 100 to 200 lbs. These are
adjusted for things like fitness, puberty, pregnancy, and stress or pain.
Compare your TDD to that in the column nearest your weight. For example,
a moderately active person who weighs 160 lbs will have a TDD close to 40
units a day. A physically fit person might need only 33 units a day, while a
woman of this weight who is pregnant in her 3rd trimester might require 74
units.

Adapted froma presentation by Lois Jovanovic, M.D., at the 2002 annual meeting of the American Association of
Clinical Endocrinologists, and fromN.S. Pierce: Diabetes and Exercise, Br J Sports Med: 161-173, 1999.
*/
public enum ThingsThatImpactYourTdd {
    
    WEIGHT_45KG_100LB(45.0, Map.ofEntries(Map.entry("New start Type 2", List.of(5.0, 11.0)), Map.entry("New start Type 1", List.of(13.0, 18.0)), Map.entry("Physically fit", List.of(20.0)), Map.entry("Moderately active", List.of(25.0)), Map.entry("Sedentary or adolescent", List.of(30.0)), Map.entry("Moderate physical stress, 2nd trim. pregnancy", List.of(36.0)), Map.entry("Greater physical stress, 3rd trim. pregnancy", List.of(40.0)), Map.entry("Severe phy. stress", List.of(45.0)), Map.entry("Infection, DKA, or steroid medication", List.of(50.0, 90.0)))),
    WEIGHT_55KG_120LB(55.0, Map.ofEntries(Map.entry("New start Type 2", List.of(6.0, 14.0)), Map.entry("New start Type 1", List.of(16.0, 22.0)), Map.entry("Physically fit", List.of(24.0)), Map.entry("Moderately active", List.of(30.0)), Map.entry("Sedentary or adolescent", List.of(37.0)), Map.entry("Moderate physical stress, 2nd trim. pregnancy", List.of(43.0)), Map.entry("Greater physical stress, 3rd trim. pregnancy", List.of(49.0)), Map.entry("Severe phy. stress", List.of(55.0)), Map.entry("Infection, DKA, or steroid medication", List.of(60.0, 108.0)))),
    WEIGHT_64KG_140LB(64.0, Map.ofEntries(Map.entry("New start Type 2", List.of(6.0, 16.0)), Map.entry("New start Type 1", List.of(19.0, 26.0)), Map.entry("Physically fit", List.of(29.0)), Map.entry("Moderately active", List.of(35.0)), Map.entry("Sedentary or adolescent", List.of(44.0)), Map.entry("Moderate physical stress, 2nd trim. pregnancy", List.of(51.0)), Map.entry("Greater physical stress, 3rd trim. pregnancy", List.of(57.0)), Map.entry("Severe phy. stress", List.of(64.0)), Map.entry("Infection, DKA, or steroid medication", List.of(70.0, 126.0)))),
    WEIGHT_73KG_160LB(73.0, Map.ofEntries(Map.entry("New start Type 2", List.of(7.0, 18.0)), Map.entry("New start Type 1", List.of(22.0, 29.0)), Map.entry("Physically fit", List.of(33.0)), Map.entry("Moderately active", List.of(40.0)), Map.entry("Sedentary or adolescent", List.of(50.0)), Map.entry("Moderate physical stress, 2nd trim. pregnancy", List.of(58.0)), Map.entry("Greater physical stress, 3rd trim. pregnancy", List.of(65.0)), Map.entry("Severe phy. stress", List.of(73.0)), Map.entry("Infection, DKA, or steroid medication", List.of(80.0, 144.0)))),
    WEIGHT_82KG_180LB(82.0, Map.ofEntries(Map.entry("New start Type 2", List.of(8.0, 20.0)), Map.entry("New start Type 1", List.of(25.0, 33.0)), Map.entry("Physically fit", List.of(37.0)), Map.entry("Moderately active", List.of(45.0)), Map.entry("Sedentary or adolescent", List.of(56.0)), Map.entry("Moderate physical stress, 2nd trim. pregnancy", List.of(66.0)), Map.entry("Greater physical stress, 3rd trim. pregnancy", List.of(74.0)), Map.entry("Severe phy. stress", List.of(82.0)), Map.entry("Infection, DKA, or steroid medication", List.of(90.0, 162.0)))),
    WEIGHT_91KG_200LB(91.0, Map.ofEntries(Map.entry("New start Type 2", List.of(9.0, 23.0)), Map.entry("New start Type 1", List.of(27.0, 36.0)), Map.entry("Physically fit", List.of(41.0)), Map.entry("Moderately active", List.of(50.0)), Map.entry("Sedentary or adolescent", List.of(62.0)), Map.entry("Moderate physical stress, 2nd trim. pregnancy", List.of(73.0)), Map.entry("Greater physical stress, 3rd trim. pregnancy", List.of(82.0)), Map.entry("Severe phy. stress", List.of(91.0)), Map.entry("Infection, DKA, or steroid medication", List.of(100.0, 180.0))));

    private final Double weight;
    private final Map<String, List<Double>> tddRanges;
    
    private ThingsThatImpactYourTdd(Double weight, Map<String, List<Double>> tddRanges) {
        this.weight = weight;
        this.tddRanges = tddRanges;
    }

    public Double getWeight() {
        return weight;
    }

    public Map<String, List<Double>> getTddRanges() {
        return tddRanges;
    }
}