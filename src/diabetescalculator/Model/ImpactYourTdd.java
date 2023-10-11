/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package diabetescalculator.Model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 *
 * @author OscarFabianHP
 */
public class ImpactYourTdd {
    
    //Lista de Enum ThingsThatImpactYourTdd
    private final List<ThingsThatImpactYourTdd> impactsEnumList = Arrays.asList(ThingsThatImpactYourTdd.WEIGHT_45KG_100LB, 
            ThingsThatImpactYourTdd.WEIGHT_55KG_120LB, ThingsThatImpactYourTdd.WEIGHT_64KG_140LB, ThingsThatImpactYourTdd.WEIGHT_73KG_160LB,
            ThingsThatImpactYourTdd.WEIGHT_82KG_180LB, ThingsThatImpactYourTdd.WEIGHT_91KG_200LB);
    
    //Lista de Claves del Map de los Enum ThingsThatImpactYourTdd
    private final List<String> keysTypesTdd = Arrays.asList("New start Type 2", "New start Type 1",  "Physically fit", 
            "Moderately active", "Sedentary or adolescent", "Moderate physical stress, 2nd trim. pregnancy", "Greater physical stress, 3rd trim. pregnancy",
            "Severe phy. stress", "Infection, DKA, or steroid medication");
    
    private Double tdd; //Dosis diaria total de insulina al día
    private Double weight; //peso actual en kilogramos

    private ThingsThatImpactYourTdd weightChosen; //Almacena el Enum ThingThatImpactYourTdd correspondiente al peso más proximo al peso del usuario
    private Map<String, List<Double>> mapChosen = Map.ofEntries();  //Map que almacena los Maps del item enum que corresponden al peso del usuario 
    private String keyChosen; //Almacena la Key (Tipo persona) del map en base al Tdd más cercano al Tdd actual del usuario
    
    public ImpactYourTdd(BigDecimal tdd, BigDecimal weight) {
        this.tdd = tdd.doubleValue();
        this.weight = weight.doubleValue();
    }

    //Metodo que se encarga de elegir lo que impacta la glucosa según peso y TDD
    private void whatImpactsYourTdd(){
        
        //Creo ListIetrator's ya que permiten mas manioblabilidad que los Iterator's que solo se mueven hacia adelante;
        ListIterator<ThingsThatImpactYourTdd> it = impactsEnumList.listIterator(); //itera en la lista que contiene todos los elementos del enum 
        ListIterator<String> itKeys = keysTypesTdd.listIterator();  //itera en la lista que contiene las claves usadas en los Map<String, List> del enum
       
        //Recorre la lista de Enum hasta encontrar el Enum más cercana al peso del usuario
        while(it.hasNext()){
            ThingsThatImpactYourTdd enumIteCurr = it.next(); //Obtiene actual Enum del ListIterator
            double weightCurr = enumIteCurr.getWeight(); //obtinen el peso del elemento enum actual
            ThingsThatImpactYourTdd enumIteNext = null; //guarda proxima Enum de ListItarator
            
            if(weight < weightCurr){//Si peso es menor al peso del Enum ThingThatImpactYourTdd
                weightChosen = enumIteCurr; //Elige el primer valor del Enum
                break;
            }
            if(it.hasNext()==true){ //verifica que no se haya llegado al final del ListIterator
                enumIteNext = it.next();//obtiene proximo Enum de ListIterator para  comparar pesos y escoger el más proximo al peso del usuario
            }             
            else if(weight > weightCurr){ //Si peso es mayor al peso del Enum ThingThatImpactYourTdd
                weightChosen = enumIteCurr; //Elige el ultimo valor del Enum
                break;
            }
            else{
                break;} //si ya comparo todos los elementos de la lista enum sale del bucle while
            
            double weightNext = enumIteNext.getWeight(); //obtiene el peso del elemento enum proximo
            
           if(weight == weightCurr) //si el peso del usuario es igual al peso de Enum Actual
            {
               weightChosen = enumIteCurr; //Almacena el Enum actual como más cercano al peso del usuario
                break; //sale del ciclo
            }
           else if(weight == weightNext){ //si el peso es igual al peso de Enum proximo
               weightChosen = enumIteNext; //Almacena el Enum Next como más cercano al peso del usuario
               break; //sale del ciclo
           }
           else if(weight > weightCurr && weight < weightNext){ //si el peso esta entre un valor entre peso del Enum Actual y el Enum Proximo 
               double closenessCurr = weight-weightCurr;
               double closenessNext = weightNext-weight;
                if(closenessCurr<closenessNext){//si peso del usuario es más cercano al Enum Actual
                    weightChosen = enumIteCurr; //Almacena el Enum actual como más cercano al peso del usuario
                    break;
                }
                else{ //si no entonces el peso del usuario es más cercano al Enum proximo
                    weightChosen = enumIteNext; //Almacena el Enum Next como más cercano al peso del usuario
                    break;
                }
            }
           else{
               it.previous(); //reposiciona el ListIterator en el elemento enum previo
           }
        }
        System.out.println("Enum Impact your Tdd: "+weightChosen);
        mapChosen =  weightChosen.getTddRanges(); //Obtiene Lista Map del Enum ThingsThatImpactYourTdd elegido en el while de arriba
        //System.out.println("Size Map of entries enum: "+mapChosen.size());
        
        //Recorre la lista Keys hasta hallar aquella que este más proximo al tdd del usuario
        while(itKeys.hasNext()){
            String keyCrr = itKeys.next(); //Obtiene siguiente Key del ListIterator -> (i)
            
            
            //List<Double> tddList = mapChosen.get(keyCrr);//obtiene la list que incluye el Map segun la key
            //Forma alternativa
            List<Double> tddListCurr = mapChosen.get(keyCrr);
            int tddListCurrSize = tddListCurr.size();
            
            if(tdd < tddListCurr.get(0)){ //si el tdd es menor que primer elemento de la lista (RddRanges) de la clave (Estado Diabetes como: "New start type 1") actual
                keyChosen = keyCrr;
                break;
            }
            
            else if(itKeys.hasNext()==true){
                String keyNxt = itKeys.next(); 
                List<Double> tddListNext = mapChosen.get(keyNxt);
                int tddListNextSize = tddListNext.size();

                
                if(tddListCurrSize > 1 && tddListNextSize > 1){ //Si la lista de rangos (TddRanges) de la clave (Estado Diabetes) actaul y proxima es de dos elementos o más
                    if(tdd >= tddListCurr.get(0) && tdd <= tddListCurr.get(1)){//Si Tdd esta entre intervalo del TddRanges de la clave Actual (Estado Diabetico)
                       keyChosen = keyCrr;
                       break; 
                    }
                    else if(tdd >= tddListNext.get(0) && tdd <= tddListNext.get(1)){//Si Tdd esta entre intervalo del TddRanges de la clave proxima (Estado Diabetico)
                        keyChosen = keyNxt;
                        break;
                    }
                    else if(tdd > tddListCurr.get(1) && tdd < tddListNext.get(0)){ //Si Tdd esta entre los intervalos (TddRanges) de la clave actual y proxima  (Estado Diabetico)
                        double closenessCurr = tdd-tddListCurr.get(1);
                        double closenessNext = tddListNext.get(0)-tdd;
                        
                        if(closenessCurr<closenessNext){ //si Tdd es mas cercano al intervalo (TddRanges) de la clave actual
                            keyChosen = keyCrr;
                            break;
                        }
                        else{
                            keyChosen = keyNxt;
                            break;
                        }
                    }
                    else{
                        itKeys.previous(); //Reposisciona iterador de Keys (Estados Diabeticos) a la posicion previa
                    }
                }
                else if(tddListCurrSize > 1 && tddListNextSize == 1){ //Si la lista de rangos (TddRanges) de la clave (Estado Diabetes) actaul es de dos elementos y la proxima de un elemento
                    if(tdd >=  tddListCurr.get(0) && tdd <= tddListCurr.get(1)){
                        keyChosen = keyCrr;
                        break;
                    }
                    else if(tdd > tddListCurr.get(1) && tdd <= tddListNext.get(0) ){
                        double closenessCurr = tdd-tddListCurr.get(1);
                        double closenessNext = tddListNext.get(0)-tdd;
                        
                        if(closenessCurr<closenessNext){
                            keyChosen = keyCrr;
                            break;
                        }
                        else{
                            keyChosen = keyNxt;
                            break;
                        }
                    }
                    else{
                        itKeys.previous();
                    }
                }
                else if(tddListCurrSize == 1 && tddListNextSize == 1){ //Si la lista de rangos (TddRanges) de la clave (Estado Diabetes) actaul y proxima es de un elemento
                    if(tdd >= tddListCurr.get(0) && tdd <= tddListNext.get(0)){
                        double closenessCurr = tdd-tddListCurr.get(0);
                        double closenessNext = tddListNext.get(0)-tdd;

                        if(closenessCurr<closenessNext){
                            keyChosen = keyCrr;
                            break;
                            }
                        else{
                            keyChosen = keyNxt;
                            break;
                            }
                        }
                    else{
                        itKeys.previous();
                    }
                }
                else if(tddListCurrSize == 1 && tddListNextSize > 1){ //Si la lista de rangos (TddRanges) de la clave (Estado Diabetes) actaul es de un elemento y proxima de dos elementos o más
                    if(tdd >= tddListCurr.get(0) && tdd < tddListNext.get(0)){
                        double closenessCurr = tdd-tddListCurr.get(0);
                        double closenessNext = tddListNext.get(0)-tdd;
                        
                        if(closenessCurr<closenessNext){
                            keyChosen = keyCrr;
                            break;
                        }
                        else{
                            keyChosen = keyNxt;
                            break;
                        }
                    }
                    else if(tdd >= tddListNext.get(0) && tdd <= tddListNext.get(1)){
                        keyChosen = keyNxt;
                        break;
                    }
                    else{
                        itKeys.previous();
                    }
                }
            }
            else if(tdd > tddListCurr.get(1)){ //No no hay más claves (Estado diabetes) por recorre, y si tdd es mayor que el ultimo elemento de la lista (tddRanges) de la clave actual (Estado Diabetes)
                keyChosen = keyCrr;
                break;
            }
        }
            /*
            //if(tddList.size()>1){
            if(keyCrr.startsWith("New start Type") || keyCrr.startsWith("Infection, DKA, or steroid medication")){
                //ListIterator<Double> itTdds = tddList.listIterator(); //Creo ListIterator de la lista de valores TDD incluidos en el Map correspondiente a la Key que se esta recorriendo con ciclo While
                List<Double> tddList = mapChosen.get(keyCrr);//obtiene la list<Double> que incluye el Map segun la key correspondiente
                //while(itTdds.hasNext()){
                   // Double tddNext = itTdds.next(); //tdd siguiente
                   System.out.println("Size list<Double> del Map: "+tddList.size());
                   Double tddAnt = tddList.get(0);
                   Double tddSig = tddList.get(1);
                    if(tdd >= tddAnt && tdd <= tddSig){ //si el Tdd del usuario se encuentra en el intervalo de tddAnt y tddSig
                        keyChosen = keyCrr;
                        break;
                    }
                    else
                        continue;
                }
            else if(itKeys.hasNext()==true){
                String keyNxt = itKeys.next(); //obtiene key proxima de ListIterator -> (i+1)
                if(!keyNxt.startsWith("New start Type") || !keyNxt.startsWith("Infection, DKA, or steroid medication")) {
                    List<Double> tddKeyCrr = mapChosen.get(keyCrr); //Obtiene lista de tdd correspodniente a la key actual
                    List<Double> tddKeyNxt = mapChosen.get(keyNxt); //Obtiene lista de tdd correspodniente a la key proxima
                    System.out.println("Size tdd NExt: "+tddKeyNxt.size()+" and Item="+tddKeyNxt.get(0));
                    if(tdd == tddKeyCrr.get(0)){
                        keyChosen = keyCrr;
                        break;
                    }
                    else if(tdd == tddKeyNxt.get(0)){
                        keyChosen = keyNxt;
                        break;
                    }
                    else if(tdd<tddKeyCrr.get(0)){
                        keyChosen = keyCrr;
                        break;
                    }
                    else if(tdd > tddKeyCrr.get(0) && tdd< tddKeyNxt.get(0)){
                        double closenessCurr = tdd - tddKeyCrr.get(0);
                        double closenessNext = tddKeyNxt.get(0) - tdd;
                        System.out.println("tdd-tddCur: "+closenessCurr);
                        System.out.println("tddNxt-tdd: "+closenessNext);

                        if(closenessCurr < closenessNext){
                            keyChosen = keyCrr;
                            break;
                        }
                        else{
                            keyChosen = keyNxt;
                            break;
                        }
                    }
                    else{
                        itKeys.previous();
                    }
                }
                else{
                    continue;
                }
            }
            else{
                it.previous();
                continue;
            }  
        }*/
    }
    //Imprime resultado correspondiente al peso y Tdd del usuario
    @Override
    public String toString() {
        whatImpactsYourTdd();
        return String.format("%s%s%n A [%s person] who weights close [%s kilograms] will have a TDD close to ( %s u/day )", 
                "Things That Impact Your TDD of ", tdd+" u/day:", keyChosen, weightChosen.getWeight(), mapChosen.get(keyChosen));
    }   
}
