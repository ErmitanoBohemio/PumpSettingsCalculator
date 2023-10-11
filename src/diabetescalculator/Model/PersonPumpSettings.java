/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package diabetescalculator.Model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils; //Importa clase Apache StringUtils que se usa para imprimir datos


/**
 *
 * @author OscarFabianHP
 */
public class PersonPumpSettings {
    private BigDecimal peso; //peso del usuario (Kg)
    private BigDecimal dosisDiariaTotalPromedio; //TDD promedio (u/day)
    private BigDecimal tddWeight; //TDD según peso (u/day)
    private BigDecimal tddStarting; //TDD inicial (u/day)
    private BigDecimal iTDD; //iTDD (TDD mejorada) segun patrones glucemico (u/day)
    private BigDecimal basalHora; //tasa basal por hora (u/hr)
    private BigDecimal basalDia;
    private BigDecimal carbF; //Factor de carbohidratos CarbF o ratio (grams/u)
    private BigDecimal corrF; //Factor de corrección CorrF o sensibilidad (mg/dl) / u
    private BigDecimal sensibilidadRelativa;
    private Double glucosaPromedio; //Glucosa promedio según A1c reciente
    private Double glucosaObjetivo; //Glucosa objetivo según indicación medica
    //private Map<Integer, Boolean> perfilGlucemico;
    private PatronGlucemico patronGlucosa;
    
    //Para especificar precion de los decimales (No se uso en este caso)
    private static final DecimalFormat decimalFormatBasal = new DecimalFormat("#.###");
    private static final DecimalFormat decimalFromatCarF = new DecimalFormat("#.#");
    
    
    
    public PersonPumpSettings(double peso, double dosisDiariaTotalPromedio, double gsPromedio, double gsObjetivo, PatronGlucemico patronGlucosa){//Map<Integer, Boolean> perfilGS){
        this.peso = new BigDecimal(peso);
        this.dosisDiariaTotalPromedio = new BigDecimal(dosisDiariaTotalPromedio).setScale(2, RoundingMode.HALF_EVEN);;
        glucosaPromedio = gsPromedio;
        glucosaObjetivo = gsObjetivo;
        this.patronGlucosa = patronGlucosa;
        
        tddStarting = calcularTDDStartingOptimizado();//calcularTDDStarting(); //Se debe calcular antes del iTDD.
        iTDD = calculariTDDbyTdd();
    }

   
    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public void setDosisDiariaTotalPromedio(BigDecimal dosisDiariaTotalPromedio) {
        this.dosisDiariaTotalPromedio = dosisDiariaTotalPromedio;
    }
    
    public void setGlucosaPromedio(Double glucosaPromedio) {
        this.glucosaPromedio = glucosaPromedio;
    }

    public void setGlucosaObjetivo(Double glucosaObjetivo) {
        this.glucosaObjetivo = glucosaObjetivo;
    }

    public void setPatronGlucosa(PatronGlucemico patronGlucosa) {
        this.patronGlucosa = patronGlucosa;
    }

    //get Starting Tdd (Tdd Esperado)
    public BigDecimal getTddStarting() {
        return tddStarting;
    }
    //get iTdd (Tdd mejorado) en base a TDD actual
    public BigDecimal getiTDD() {
        return iTDD;
    }
    
    //get TDD actual
    public BigDecimal getDosisDiariaTotalPromedio() {
        return dosisDiariaTotalPromedio;
    }

    public BigDecimal getPeso() {
        return peso;
    }
    /*
    //Se uso en la version 2.0 pero en la ultima version en su lugar se crean metodos get para Tdd, Starting Tdd y iTdd para crear instancias separadas de BasalRatesRecommended;
    public BasalRatesRecommended calcularTasasBasales(){
        BasalRatesRecommended basalRates = new BasalRatesRecommended(iTDD); //Almacena los valores basales recomendados segun % basal y tdd
        return basalRates;
    }
     */   
    //Calcula el TDD esperado con base en el peso
    private BigDecimal calcularTddPeso(){
        //Si el peso corresponde al peso de un niño o niña calcular tdd esperado para peso niños
        if(peso.doubleValue() <= 47.0) 
            /*
            Before puberty, children often need less insulin, so a young child’s weight is
            multiplied by 0.20 u/lb (or by 0.44 u/kg for kilograms) instead of 0.24 u/lb.
            En lo que se refiere al peso, los niños ganan de 4 a 7 libras (de 2 a 3 kg) cada año hasta que inician la pubertad.
            En promedio un niño de 8 años pesa 23.5 Kg
            */
            tddWeight = peso.multiply(BigDecimal.valueOf(0.44));
        else // de lo contrario calcular el tdd esperado para adultos en general    
            tddWeight = peso.multiply(BigDecimal.valueOf(0.53));//.setScale(1, RoundingMode.HALF_EVEN);
        System.out.println("TDD PESO: "+tddWeight);
        return tddWeight;
    }
    
    /*
    CompareTo nos devolverá 3 valores dependiendo lo que queramos comparar:

        1  Cuando el primer valor BigDecimal sea mayor que el segundo valor BigDecimal.
        0  Cuando el primer valor BigDecimal sea igual que el segundo valor BigDecimal.
        -1 Cuando el primer valor BigDecimal sea menor que el segundo valor BigDecimal.
    */
     //Calcula el TDD mejorado o iTDD(EN BASE AL TDD ACTUAL)
    private BigDecimal calculariTDDbyTdd(){
        if(PatronGlucemico.BAJO == patronGlucosa || PatronGlucemico.BAJO_ALTO == patronGlucosa){
            //Lower Your TDD 5% for Frequent Lows and Frequent Highs
            iTDD = dosisDiariaTotalPromedio.multiply(BigDecimal.valueOf(0.95)).setScale(1, RoundingMode.HALF_EVEN); //de lo contrario usa el TDD actual para calcular el iTDD
        }
        else if(PatronGlucemico.MUY_BAJO == patronGlucosa){
            //Lower Your TDD 10% for Frequent Lows (Usa el valor TDD menor entre el Actual TDD y Starting TDD)
            iTDD = dosisDiariaTotalPromedio.multiply(BigDecimal.valueOf(0.9)).setScale(1, RoundingMode.HALF_EVEN); //de lo contrario usa el TDD actual para calcular el iTDD
        }
        else if(PatronGlucemico.ALTO == patronGlucosa && glucosaPromedio>160 && glucosaPromedio>glucosaObjetivo){
            //Raise the TDD by 1% for every 6 mg/dL (0.33 mmol/L) you want to the average glucose (Usa el valor TDD meyor entre el Actual TDD y Starting TDD)
            double raisePercentage = 1 + ((glucosaPromedio - glucosaObjetivo)/6)/100; //redondea a dos decimales el resultado de la operacion y el resultado final daría ej: si TDD es 40u, GlucosaPromedio 200, GlucosaObjetivo 140 entonces: 200-140=60mg/dl y segun la regla para patron de Hiper se aumenta 1% el TDD por cada 6 mg/dl que se desee bajar el promedio de glucosa entonces segun la regla el porcentaje a aumentar TDD es: 60/6=10 entonces 1% * 10= 10% por lo que el iTDD daria de multiplicar TDD*1.10
                iTDD = dosisDiariaTotalPromedio.multiply(BigDecimal.valueOf(raisePercentage)).setScale(1, RoundingMode.HALF_EVEN); //Prefiere el TDD actual para calcular el iTDD
        }
        else if(PatronGlucemico.NORMAL == patronGlucosa || PatronGlucemico.ALTO == patronGlucosa){ //Si el patron de glucosa es NORMAL
            if(glucosaPromedio>glucosaObjetivo){ //y si glucosa en sangre promedio es mayor a la glucosa objetivo y glucosa promedio mayor a 154 mg/dl, se calcula un iTDD para buscar mejorar la glucosa promedio
                double raisePercentage = 1 + (((glucosaPromedio - glucosaObjetivo)/6)/100); //Raise the TDD by 1% for every 6 mg/dL (0.33 mmol/L) you want to lower the average glucose
                iTDD = dosisDiariaTotalPromedio.multiply(BigDecimal.valueOf(raisePercentage)).setScale(1, RoundingMode.HALF_EVEN); //Prefiere el TDD actual para calcular el iTDD
            }
            else { //Si su glucosa promedio esta dentro del rango de glucosa objetivo, 
                //o si  glucosa objetivo es mayor a glucosa promedio (es decir no requiere ajuste) entonces el iTDD será igual al TDD
                    iTDD = dosisDiariaTotalPromedio; //Prefiere el TDD actual
                }
        }
        System.out.println("iTDD: "+iTDD);
        return iTDD;
    }
    
    //Calcula el TDD mejorado (Teniendo en cuenta patron glucemico) YA NO SE USA PORQUE EL iTDD SE DEBE CALCULAR EN BASE AL TDD ACTUAL
    private BigDecimal calculariTDD(){
        if(PatronGlucemico.BAJO == patronGlucosa || PatronGlucemico.BAJO_ALTO == patronGlucosa){
            //Lower Your TDD 5% for Frequent Lows and Frequent Highs
            if(tddStarting.compareTo(dosisDiariaTotalPromedio)<0){ //Si el "Starting TDD" es menor que el "TDD actual"
                iTDD = tddStarting.multiply(BigDecimal.valueOf(0.95)).setScale(1, RoundingMode.HALF_EVEN); //Prefiere el Starting TDD para calcular iTDD
                System.out.println("Uso star TDD x bajo = "+tddStarting);
            }
            else {
                iTDD = dosisDiariaTotalPromedio.multiply(BigDecimal.valueOf(0.95)).setScale(1, RoundingMode.HALF_EVEN); //de lo contrario usa el TDD actual para calcular el iTDD
                System.out.println("Uso actual TDD x bajo = "+dosisDiariaTotalPromedio);

            }
        }
        else if(PatronGlucemico.MUY_BAJO == patronGlucosa){
            //Lower Your TDD 10% for Frequent Lows (Usa el valor TDD menor entre el Actual TDD y Starting TDD)
            if(tddStarting.compareTo(dosisDiariaTotalPromedio)<0) {//Si el "Starting TDD" es menor que el "TDD actual"
                iTDD = tddStarting.multiply(BigDecimal.valueOf(0.9)).setScale(1, RoundingMode.HALF_EVEN); //Prefiere el Starting TDD para calcular iTDD
                System.out.println("Uso star TDD x muy bajo = "+tddStarting);

            }
            else{
                iTDD = dosisDiariaTotalPromedio.multiply(BigDecimal.valueOf(0.9)).setScale(1, RoundingMode.HALF_EVEN); //de lo contrario usa el TDD actual para calcular el iTDD
                System.out.println("Uso actual TDD x muy bajo = "+dosisDiariaTotalPromedio);    
            }
        }
        else if(PatronGlucemico.ALTO == patronGlucosa && glucosaPromedio>160 && glucosaPromedio>glucosaObjetivo){
            //Raise the TDD by 1% for every 6 mg/dL (0.33 mmol/L) you want to the average glucose (Usa el valor TDD meyor entre el Actual TDD y Starting TDD)
            double raisePercentage = 1 + ((glucosaPromedio - glucosaObjetivo)/6)/100; //redondea a dos decimales el resultado de la operacion y el resultado final daría ej: si TDD es 40u, GlucosaPromedio 200, GlucosaObjetivo 140 entonces: 200-140=60mg/dl y segun la regla para patron de Hiper se aumenta 1% el TDD por cada 6 mg/dl que se desee bajar el promedio de glucosa entonces segun la regla el porcentaje a aumentar TDD es: 60/6=10 entonces 1% * 10= 10% por lo que el iTDD daria de multiplicar TDD*1.10
            if(dosisDiariaTotalPromedio.compareTo(tddStarting)>0) {//Si el "TDD Actual" es mayor que el "Starting TDD"
                iTDD = dosisDiariaTotalPromedio.multiply(BigDecimal.valueOf(raisePercentage)).setScale(1, RoundingMode.HALF_EVEN); //Prefiere el TDD actual para calcular el iTDD
                System.out.println("Uso actual TDD x alto = "+dosisDiariaTotalPromedio);
            }
            else{    
                iTDD = tddStarting.multiply(BigDecimal.valueOf(raisePercentage)).setScale(1, RoundingMode.HALF_EVEN); //de lo contrario usa el Starting TDD para el calculo de iTDD
                System.out.println("Uso start TDD  x alto = "+tddStarting);

            }
        }
        else if(PatronGlucemico.NORMAL == patronGlucosa /*|| PatronGlucemico.ALTO == patronGlucosa*/){ //Si el patron de glucosa es NORMAL
            if(glucosaPromedio>glucosaObjetivo && glucosaPromedio>154){ //y si glucosa en sangre promedio es mayor a la glucosa objetivo y glucosa promedio mayor a 154 mg/dl, se calcula un iTDD para buscar mejorar la glucosa promedio
                double raisePercentage = 1 + (((glucosaPromedio - glucosaObjetivo)/6)/100); //Raise the TDD by 1% for every 6 mg/dL (0.33 mmol/L) you want to lower the average glucose

                if(dosisDiariaTotalPromedio.compareTo(tddStarting)>0){ //Si TDD actual es mayor que el Starting TDD
                    //iTDD = tddStarting.multiply(BigDecimal.valueOf(raisePercentage)).setScale(1, RoundingMode.HALF_EVEN);
                    iTDD = dosisDiariaTotalPromedio.multiply(BigDecimal.valueOf(raisePercentage)).setScale(1, RoundingMode.HALF_EVEN); //Prefiere el TDD actual para calcular el iTDD
                    System.out.println("Uso actual TDD x anormal = "+dosisDiariaTotalPromedio);
                }
                else{
                    iTDD = tddStarting.multiply(BigDecimal.valueOf(raisePercentage)).setScale(1, RoundingMode.HALF_EVEN); //prefiere el Starting TDD para calcular el iTDD
                    System.out.println("Uso start TDD  x anormal = "+tddStarting);
                }
            }
            else { //Si su glucosa promedio esta dentro del rango de glucosa objetivo, 
                //o si  glucosa objetivo es mayor a glucosa promedio (es decir no requiere ajuste) entonces el iTDD será igual al TDD
                if(dosisDiariaTotalPromedio.compareTo(tddStarting)<0){//entonces si TDD actual es menor al Starting TDD
                    iTDD = dosisDiariaTotalPromedio; //Prefiere el TDD actual
                    System.out.println("Uso actual TDD x normal = "+dosisDiariaTotalPromedio);
                }
                else{
                    iTDD  = tddStarting; //de lo contrario usa el Starting TDD para calcular el iTDD
                //iTDD  = dosisDiariaTotalPromedio;
                    System.out.println("Uso start TDD  x normal = "+tddStarting);

                }
            }
        }
        System.out.println("iTDD = "+iTDD);
        return iTDD;
    }
    
    //Calcular iTDD ver. TDD e TDD Starting (NO SE USA ACTUALMENTE)
     private BigDecimal calculariTDD(BigDecimal tdd){
        if(PatronGlucemico.BAJO == patronGlucosa || PatronGlucemico.BAJO_ALTO == patronGlucosa)
            //Lower Your TDD 5% for Frequent Lows and Frequent Highs
            //iTDD = tddStarting.multiply(BigDecimal.valueOf(0.95)).setScale(1, RoundingMode.HALF_EVEN);
            iTDD = tdd.multiply(BigDecimal.valueOf(0.95)).setScale(1, RoundingMode.HALF_EVEN);
        else if(PatronGlucemico.MUY_BAJO == patronGlucosa)
            //Lower Your TDD 10% for Frequent Lows
            //iTDD = tddStarting.multiply(BigDecimal.valueOf(0.9)).setScale(1, RoundingMode.HALF_EVEN);
            iTDD = tdd.multiply(BigDecimal.valueOf(0.9)).setScale(1, RoundingMode.HALF_EVEN);
        else if(PatronGlucemico.ALTO == patronGlucosa && (glucosaPromedio>=160 && glucosaObjetivo<glucosaPromedio)){
            //Raise the TDD by 1% for every 6 mg/dL (0.33 mmol/L) you want to the average glucose
            double raisePercentage = 1 + ((glucosaPromedio - glucosaObjetivo)/6)/100; //redondea a dos decimales el resultado de la operacion y el resultado final daría ej: si TDD es 40u, GlucosaPromedio 200, GlucosaObjetivo 140 entonces: 200-140=60mg/dl y segun la regla para patron de Hiper se aumenta 1% el TDD por cada 6 mg/dl que se desee bajar el promedio de glucosa entonces segun la regla el porcentaje a aumentar TDD es: 60/6=10 entonces 1% * 10= 10% por lo que el iTDD daria de multiplicar TDD*1.10
            //iTDD = tddStarting.multiply(BigDecimal.valueOf(raisePercentage)).setScale(1, RoundingMode.HALF_EVEN);
            iTDD = tdd.multiply(BigDecimal.valueOf(raisePercentage)).setScale(1, RoundingMode.HALF_EVEN);
            System.out.println("Lasr ver. Glucosa alta "+iTDD );
        }
        else if(PatronGlucemico.NORMAL == patronGlucosa || PatronGlucemico.ALTO == patronGlucosa){ //Si el patron de glucosa es NORMAL
            if(glucosaPromedio>glucosaObjetivo){ //y si glucosa en sangre promedio es mayor a la glucosa objetivo, se calcula un iTDD para buscar mejorar la glucosa promedio
                double raisePercentage = 1 + (((glucosaPromedio - glucosaObjetivo)/6)/100); //Raise the TDD by 1% for every 6 mg/dL (0.33 mmol/L) you want to lower the average glucose
                //iTDD = tddStarting.multiply(BigDecimal.valueOf(raisePercentage)).setScale(1, RoundingMode.HALF_EVEN);
                iTDD = tdd.multiply(BigDecimal.valueOf(raisePercentage)).setScale(1, RoundingMode.HALF_EVEN);
                System.out.println("Lasr ver. TDD por rango desajunstado "+iTDD);
            }
            else  //Si su glucosa promedio esta dentro del rango de glucosa objetivo, 
                //o si  glucosa objetivo es mayor a glucosa promedio (es decir no requiere ajuste) entonces el iTDD será igual al TDD
                //iTDD  = tddStarting;
                iTDD  = tdd;
        }
        System.out.println("Lasr ver. iTDD="+iTDD);
        return iTDD;
    }
    
    //Calcula el TDD de inicio (Sin tener encuenta patron glucemico)-NO SE USA ACTUALMENTE-
    private BigDecimal calcularTDDStarting(){
        tddStarting = dosisDiariaTotalPromedio.add(calcularTddPeso()).multiply(BigDecimal.valueOf(0.45)).setScale(1, RoundingMode.HALF_EVEN);
        System.out.println("Tdd Starting: "+tddStarting);
        return tddStarting;
    }
    
    //Starting Tdd que escoje el valor más adecuado de Starting TDD teniendo encuenta Patrones de glucosa. 
    private BigDecimal calcularTDDStartingOptimizado(){
        //Este starTdd ejecuta además el metodo q realiza calculo del Weight TDD para poder calcular el Starting TDD.
        BigDecimal startTdd = dosisDiariaTotalPromedio.add(calcularTddPeso()).multiply(BigDecimal.valueOf(0.45)).setScale(1, RoundingMode.HALF_EVEN);  //Ejecuta el metodo para calcular el TDD Weight

        if(PatronGlucemico.ALTO == patronGlucosa || (glucosaPromedio>160 && PatronGlucemico.NORMAL == patronGlucosa && glucosaPromedio>glucosaObjetivo)){
            //Si presenta Patron ALTO o glucosa fuera de rango objetivos y mayor a 160 mg/dl prefiere el TDD mayor entre el TDD Weight y Starting TDD
            if(startTdd.compareTo(tddWeight)<0){
                tddStarting = tddWeight;
                System.out.println("TDD Starting por HIPER: "+tddStarting);
            }
            else{
                tddStarting = startTdd;
                System.out.println("Tdd Starting por descarte HIPER: "+tddStarting);
            }
        }
        else if(PatronGlucemico.BAJO == patronGlucosa || PatronGlucemico.MUY_BAJO == patronGlucosa 
                || PatronGlucemico.BAJO_ALTO == patronGlucosa){
            //Si presenta Patron BAJO, MUY_BAJO o BAJO_ALTO 
            //Prefiere TDD Weight si presenta GS Promedio mayor q 160, TDD actual mayor al TDD Weight y Starting TDD menor al Tdd Weight 
            if(dosisDiariaTotalPromedio.compareTo(tddWeight)>0 && startTdd.compareTo(tddWeight)<0 && glucosaPromedio>160){
                tddStarting = tddWeight;
                System.out.println("TDD Starting por HIPO: "+tddStarting);
            }
            else{
                tddStarting = startTdd;
                System.out.println("Tdd Starting por descarte HIPOS: "+tddStarting);
            }
        }
        else {
            tddStarting = startTdd;
            System.out.println("Tdd Starting por descarte: "+tddStarting);
        }
        return tddStarting;
    }
    
    //Calcula la Basal u/day (ej: 7.7)
    private BigDecimal calcularBasalDia(){
        basalDia = basalHora.multiply(BigDecimal.valueOf(24)).setScale(1, RoundingMode.HALF_EVEN);
        return basalDia;
    }
    
    //Ver. TDD y iTDD
    private BigDecimal calcularBasalDia(BigDecimal tdd){
        BigDecimal basalDay = tdd.multiply(BigDecimal.valueOf(0.02)).multiply(BigDecimal.valueOf(24)).setScale(1, RoundingMode.HALF_EVEN);
        return basalDay;
    }
    
    //Calcula la Basal u/hr (ej: 0.70)
    private BigDecimal calcularBasalHora(){
        basalHora = iTDD.multiply(BigDecimal.valueOf(0.02)).setScale(3, RoundingMode.HALF_EVEN);
        return basalHora;
    }
    
    //Ver. TDD y iTDD
    private BigDecimal calcularBasalHora(BigDecimal tdd){
        BigDecimal basalHour = tdd.multiply(BigDecimal.valueOf(0.02)).setScale(3, RoundingMode.HALF_EVEN); 
        return basalHour;
    }
    
    //Calcula el CarbF o Ratio (ej: 17.9)
    private BigDecimal calcularCarbF(){
        carbF = peso.multiply(BigDecimal.valueOf(5.7)).divide(iTDD, 1, RoundingMode.HALF_EVEN).setScale(1, RoundingMode.HALF_EVEN);//, 1, RoundingMode.HALF_EVEN);//.setScale(1, RoundingMode.HALF_EVEN);
        return carbF;
    }
    
    //Ver. TDD y iTDD
    private BigDecimal calcularCarbF(BigDecimal tdd){
        BigDecimal carbFactor = peso.multiply(BigDecimal.valueOf(5.7)).divide(tdd, RoundingMode.HALF_EVEN).setScale(1, RoundingMode.HALF_EVEN);
        return carbFactor;
    }
    
    //Calcula el CorrF o Corrección (ej: 81.7)
    private BigDecimal calcularCorrF(){
        if(PatronGlucemico.ALTO == patronGlucosa || (PatronGlucemico.NORMAL == patronGlucosa && glucosaPromedio>160))
            //Se redondea el divisor y luego el resultado para evitar la Exception=> Caused by: java.lang.ArithmeticException: Non-terminating decimal expansion; no exact representable decimal result.
            corrF = BigDecimal.valueOf(1800).divide(iTDD, 1, RoundingMode.HALF_EVEN).setScale(1, RoundingMode.HALF_EVEN);
        else    
            corrF = BigDecimal.valueOf(1960).divide(iTDD, 1, RoundingMode.HALF_EVEN).setScale(1, RoundingMode.HALF_EVEN);
        return corrF;
    }
    
    //Ver. TDD y iTDD
    private BigDecimal calcularCorrF(BigDecimal tdd){
        BigDecimal corrFactor = new BigDecimal(0);
        if(PatronGlucemico.ALTO == patronGlucosa || (PatronGlucemico.NORMAL == patronGlucosa && glucosaPromedio>160))
            //Se redondea el divisor y luego el resultado para evitar la Exception=> Caused by: java.lang.ArithmeticException: Non-terminating decimal expansion; no exact representable decimal result.
            corrFactor = BigDecimal.valueOf(1800).divide(tdd, 1, RoundingMode.HALF_EVEN).setScale(1, RoundingMode.HALF_EVEN);
        else    
            corrFactor = BigDecimal.valueOf(1960).divide(tdd, 1, RoundingMode.HALF_EVEN).setScale(1, RoundingMode.HALF_EVEN);
        return corrFactor;
    }
    
    //Calcula la IS relative en forma de porcentaje Asi: IS=1,22 then IS(%)=122%
    private BigDecimal calcularSensibilidadRelativa(){
        sensibilidadRelativa = peso.multiply(BigDecimal.valueOf(0.53)).divide(iTDD, 2, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100));//, 2, RoundingMode.HALF_EVEN);
        return sensibilidadRelativa;
    }
    
    //Ver. TDD y iTDD
    private BigDecimal calcularSensibilidadRelativa(BigDecimal tdd){
        BigDecimal is = peso.multiply(BigDecimal.valueOf(0.53)).divide(tdd, 2, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100));//, 2, RoundingMode.HALF_EVEN);
        return is;
    }
    /*
    //Se uso en la Version 1.
    @Override
    public String toString() {
        return String.format("-------CONFIGURACIÓN OPTIMA PARA BOMBA DE INSULINA-------%n%n"
                + "TDD DE INICIO ESPERADO (TDD): %.1f u/dia%n%n" 
                + "TDD MEJORADO (iTDD): %.1f u/dia%n%n" 
                + "BASAL HORA: %.3f u/hr%n%n" 
                + "BASAL DÍA: %.1f u/dia%n%n"
                + "CARB FACTOR (CarbF): %.1f gramos por unidad de insulina%n%n" 
                + "CORRECTION FACTOR (CorrF): %.1f mg/gl por unidad de insulina%n%n" 
                + "SENSIBILIDAD RELATIVA A INSULINA (IS): %.0f%%%n%n%n" 
                ,calcularTDDStarting().doubleValue(), calculariTDD().doubleValue(), calcularBasalHora().doubleValue(), calcularBasalDia().doubleValue(), calcularCarbF().doubleValue(), calcularCorrF().doubleValue(), calcularSensibilidadRelativa().doubleValue() );
    }
    */
    //Muestra valores de configuración recomendados para la bomba de insulina en base a TDD Actual, TDD Esperado y TDD Mejorado.
     public String toString2() {
        return String.format("%85s%n%n"
                + "%s|%s|%s|%s| %n"
                + "%s|%s|%s|%s| %n"
                + "%s|%s|%s|%s| %n" 
                + "%s|%s|%s|%s| %n"
                + "%s|%s|%s|%s| %n" 
                + "%s|%s|%s|%s| %n" 
                + "%s|%s|%s|%s| %n%n",
                "««« CONFIGURACIÓN OPTIMA PARA BOMBA DE INSULINA »»»",
                StringUtils.center("", 30, "_"), StringUtils.center("ACTUAL", 25, "_"), StringUtils.center("ESPERADA", 25, "_"), StringUtils.center("MEJORADA", 25, "_"),
                StringUtils.rightPad("TDD:", 30, "_"), StringUtils.center(getDosisDiariaTotalPromedio().doubleValue() + " u/dia", 25, "_") , StringUtils.center(/*calcularTDDStartingOptimizado()*/getTddStarting().doubleValue() + " u/dia", 25, "_"), StringUtils.center(/*calculariTDD()*/getiTDD().doubleValue() + " u/dia", 25, "_"),
                StringUtils.rightPad("BASAL HORA:", 30, "_"), StringUtils.center(calcularBasalHora(dosisDiariaTotalPromedio).doubleValue() + " u/hr", 25, "_"), StringUtils.center(calcularBasalHora(tddStarting).doubleValue() + " u/hr", 25, "_"), StringUtils.center(calcularBasalHora().doubleValue() + " u/hr", 25, "_"),
                StringUtils.rightPad("BASAL DÍA:", 30, "_"), StringUtils.center(calcularBasalDia(dosisDiariaTotalPromedio).doubleValue() + " u/dia", 25, "_"), StringUtils.center(calcularBasalDia(tddStarting).doubleValue() + " u/dia", 25, "_"), StringUtils.center(calcularBasalDia().doubleValue() + " u/dia", 25, "_"),
                StringUtils.rightPad("CARB FACTOR (CarbF):", 30, "_"), StringUtils.center(calcularCarbF(dosisDiariaTotalPromedio).doubleValue() + " gramos por unidad", 25, "_"), StringUtils.center(calcularCarbF(tddStarting).doubleValue() + " gramos por unidad", 25, "_"), StringUtils.center(calcularCarbF().doubleValue() + " gramos por unidad", 25, "_"),
                StringUtils.rightPad("CORRECTION FACTOR (CorrF):", 30, "_"), StringUtils.center(calcularCorrF(dosisDiariaTotalPromedio).doubleValue() + " mg/dl por unidad", 25, "_"), StringUtils.center(calcularCorrF(tddStarting).doubleValue() + " mg/dl por unidad", 25, "_"), StringUtils.center(calcularCorrF().doubleValue() + " mg/dl por unidad", 25, "_"),
                StringUtils.rightPad("SENSIBILIDAD A INSULINA (IS):", 30, "_"), StringUtils.center(calcularSensibilidadRelativa(dosisDiariaTotalPromedio).setScale(0) + " %", 25, "_"), StringUtils.center(calcularSensibilidadRelativa(tddStarting).setScale(0) + " %", 25, "_"), StringUtils.center(calcularSensibilidadRelativa().setScale(0) + " %", 25, "_"));
    }
}
