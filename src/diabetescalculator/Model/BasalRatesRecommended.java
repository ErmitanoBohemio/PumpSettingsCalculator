/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package diabetescalculator.Model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils; //Importa clase de Apache StringUtils usada para imprimir datos

/**
 *
 * @author OscarFabianHP
 */
public class BasalRatesRecommended{
    
    //Maps que almacenan las tasas basales para cada tipo TDD (TDD, Start TDD e iTDD)
    private Map<AvgBasalRates, BigDecimal> basalRatesForTdd; //Map Tasa basal TDD actual con clave enum tasas y  valor Tdd*Tasa
    private Map<AvgBasalRates, BigDecimal> basalRatesForiTdd; //Map Tasa basal TDD Mejorado con clave enum tasas y  valor Tdd*Tasa
    private Map<AvgBasalRates, BigDecimal> basalRatesForStartTdd; //Map Tasa basal TDD esperado con clave enum tasas y  valor Tdd*Tasa
    /*
    //ya no se usan en la ver. 3, se usan cuando se ejecuta el metodo toString2()
    private List<Double> corrFUsuallyTdd; //arraylist CorrF para TDD Actual que almacena los corrF calculado en orden ascendente
    private List<Double> corrFUsuallyStartTdd; //arraylist CorrF para TDD Esperado que almacena los corrF calculado en orden ascendente
    private List<Double> corrFUsuallyiTdd; //arraylist CorrF para TDD mejorado que almacena los corrF calculado en orden ascendente
    */
    //Maps que almacenan los CorrF para cada tipo TDD (TDD, Star TDD e iTDD)
    private Map<CorrFSegunA1c, List<Double>> corrFTdd; //Map CorrF para TDD con Clave enum CorrF y valores corrF correspondientes
    private Map<CorrFSegunA1c, List<Double>> corrFStartTdd; //Map CorrF para TDD Esperado con Clave enum CorrF y valores corrF correspondientes 
    private Map<CorrFSegunA1c, List<Double>> corrFiTdd; //Map CorrF para TDD Mejorado con Clave enum CorrF y valores corrF correspondientes

    private BigDecimal iTdd; //Almacena TDD Mejorado (iTDD) calculado en PersonPumpSettings necesario para realizar los calculos basal y corrF
    private BigDecimal tdd; //Almacena Tdd actual (TDD) calculado en PersonPumpSettings
    private BigDecimal startTdd; //Almacena Tdd esperado (StartTDD) calculado en PersonPumpSettings
    /*
    //Se uso en la version beta del programa donde solo se calculada los valores según iTDD
    public BasalRatesRecommended(BigDecimal iTdd){
     this.iTdd = iTdd;
     //declara y añade en el Map los basales empezando por tasa basal del 40% de Tdd y terminando en basal del 60% de tdd.
     basalRatesForTdd = new HashMap<>(5); //
     basalRatesForTdd.put(AvgBasalRates.CUARENTA, avgBasalRateHourForTDD(iTdd, AvgBasalRates.CUARENTA));
     basalRatesForTdd.put(AvgBasalRates.CUARENTAYCINCO, avgBasalRateHourForTDD(iTdd, AvgBasalRates.CUARENTAYCINCO));
     basalRatesForTdd.put(AvgBasalRates.CINCUENTA, avgBasalRateHourForTDD(iTdd, AvgBasalRates.CINCUENTA));
     basalRatesForTdd.put(AvgBasalRates.CINCUENTAYCINCO, avgBasalRateHourForTDD(iTdd, AvgBasalRates.CINCUENTAYCINCO));
     basalRatesForTdd.put(AvgBasalRates.SESENTA, avgBasalRateHourForTDD(iTdd, AvgBasalRates.SESENTA)); 
     
     //declara y añade los corrF ascendente empezando por A1c >10% hasta <6.6%
     corrFUsuallyTdd = new ArrayList<Double>(5);
     corrFUsuallyTdd.add(corrFUsually(iTdd, CorrFSegunA1c.MAYORQ10));
     corrFUsuallyTdd.add(corrFUsually(iTdd, CorrFSegunA1c.OCHOYDIEZ));
     corrFUsuallyTdd.add(corrFUsually(iTdd, CorrFSegunA1c.SIETEYOCHO));
     corrFUsuallyTdd.add(corrFUsually(iTdd, CorrFSegunA1c.SEISSEISYSIETE));
     corrFUsuallyTdd.add(corrFUsually(iTdd, CorrFSegunA1c.MENORQSEISSEIS));
    }
    */
    //Se usa para calcular los resultados de Rangos usuales de CorrF, CarbF y Basales para TDD, TDD Esperado y TDD Mejorado.
    public BasalRatesRecommended(BigDecimal iTdd, BigDecimal tdd, BigDecimal startTdd) {
        this.iTdd = iTdd;
        this.tdd = tdd;
        this.startTdd = startTdd;
        
        //declara y añade en el Map los basales empezando por tasa basal del 40% de Tdd y terminando en basal del 60% de tdd.
        basalRatesForTdd = new HashMap<>(5);
        basalRatesForStartTdd = new HashMap<>(5);
        basalRatesForiTdd = new HashMap<>(5);
        
        //inicializa Map de tasas basales TDD
        basalRatesForTdd.put(AvgBasalRates.CUARENTA, avgBasalRateHourForTDD(tdd, AvgBasalRates.CUARENTA));
        basalRatesForTdd.put(AvgBasalRates.CUARENTAYCINCO, avgBasalRateHourForTDD(tdd, AvgBasalRates.CUARENTAYCINCO));
        basalRatesForTdd.put(AvgBasalRates.CINCUENTA, avgBasalRateHourForTDD(tdd, AvgBasalRates.CINCUENTA));
        basalRatesForTdd.put(AvgBasalRates.CINCUENTAYCINCO, avgBasalRateHourForTDD(tdd, AvgBasalRates.CINCUENTAYCINCO));
        basalRatesForTdd.put(AvgBasalRates.SESENTA, avgBasalRateHourForTDD(tdd, AvgBasalRates.SESENTA));
        
        //inicializa Map de tasas basales Starting TDD
        basalRatesForStartTdd.put(AvgBasalRates.CUARENTA, avgBasalRateHourForTDD(startTdd, AvgBasalRates.CUARENTA));
        basalRatesForStartTdd.put(AvgBasalRates.CUARENTAYCINCO, avgBasalRateHourForTDD(startTdd, AvgBasalRates.CUARENTAYCINCO));
        basalRatesForStartTdd.put(AvgBasalRates.CINCUENTA, avgBasalRateHourForTDD(startTdd, AvgBasalRates.CINCUENTA));
        basalRatesForStartTdd.put(AvgBasalRates.CINCUENTAYCINCO, avgBasalRateHourForTDD(startTdd, AvgBasalRates.CINCUENTAYCINCO));
        basalRatesForStartTdd.put(AvgBasalRates.SESENTA, avgBasalRateHourForTDD(startTdd, AvgBasalRates.SESENTA));
        
        //inicializa Map de tasas basales iTDD
        basalRatesForiTdd.put(AvgBasalRates.CUARENTA, avgBasalRateHourForTDD(iTdd, AvgBasalRates.CUARENTA));
        basalRatesForiTdd.put(AvgBasalRates.CUARENTAYCINCO, avgBasalRateHourForTDD(iTdd, AvgBasalRates.CUARENTAYCINCO));
        basalRatesForiTdd.put(AvgBasalRates.CINCUENTA, avgBasalRateHourForTDD(iTdd, AvgBasalRates.CINCUENTA));
        basalRatesForiTdd.put(AvgBasalRates.CINCUENTAYCINCO, avgBasalRateHourForTDD(iTdd, AvgBasalRates.CINCUENTAYCINCO));
        basalRatesForiTdd.put(AvgBasalRates.SESENTA, avgBasalRateHourForTDD(iTdd, AvgBasalRates.SESENTA));

        //Declara los Maps que almacenan el par clave valor (Clave=Rango A1c y Valor=constante/TDD)
        corrFTdd = new HashMap<>(5);
        corrFStartTdd = new HashMap<>(5);
        corrFiTdd = new HashMap<>(5);
        
        //declara los Maps correspondientes a TDD, Starting TDD y iTDD con el valores CorrF correspondientes
        corrFTdd.put(CorrFSegunA1c.MENORQSEISSEIS, corrF(tdd, CorrFSegunA1c.MENORQSEISSEIS));
        corrFTdd.put(CorrFSegunA1c.SEISSEISYSIETE, corrF(tdd, CorrFSegunA1c.SEISSEISYSIETE));
        corrFTdd.put(CorrFSegunA1c.SIETEYOCHO, corrF(tdd, CorrFSegunA1c.SIETEYOCHO));
        corrFTdd.put(CorrFSegunA1c.OCHOYDIEZ, corrF(tdd, CorrFSegunA1c.OCHOYDIEZ));
        corrFTdd.put(CorrFSegunA1c.MAYORQ10, corrF(tdd, CorrFSegunA1c.MAYORQ10));

        corrFStartTdd.put(CorrFSegunA1c.MENORQSEISSEIS, corrF(startTdd ,CorrFSegunA1c.MENORQSEISSEIS));
        corrFStartTdd.put(CorrFSegunA1c.SEISSEISYSIETE, corrF(startTdd ,CorrFSegunA1c.SEISSEISYSIETE));
        corrFStartTdd.put(CorrFSegunA1c.SIETEYOCHO, corrF(startTdd ,CorrFSegunA1c.SIETEYOCHO));
        corrFStartTdd.put(CorrFSegunA1c.OCHOYDIEZ, corrF(startTdd ,CorrFSegunA1c.OCHOYDIEZ));
        corrFStartTdd.put(CorrFSegunA1c.MAYORQ10, corrF(startTdd ,CorrFSegunA1c.MAYORQ10));
        
        corrFiTdd.put(CorrFSegunA1c.MENORQSEISSEIS, corrF(iTdd, CorrFSegunA1c.MENORQSEISSEIS));
        corrFiTdd.put(CorrFSegunA1c.SEISSEISYSIETE, corrF(iTdd, CorrFSegunA1c.SEISSEISYSIETE));
        corrFiTdd.put(CorrFSegunA1c.SIETEYOCHO, corrF(iTdd, CorrFSegunA1c.SIETEYOCHO));
        corrFiTdd.put(CorrFSegunA1c.OCHOYDIEZ, corrF(iTdd, CorrFSegunA1c.OCHOYDIEZ));
        corrFiTdd.put(CorrFSegunA1c.MAYORQ10, corrF(iTdd, CorrFSegunA1c.MAYORQ10));
        /*
        //se uso en la version 2, a partir de la version 3 ya no se usa
        //declara y añade los corrF ascendente empezando por A1c >10% hasta <6.6%
        corrFUsuallyTdd = new ArrayList<Double>(5);
        corrFUsuallyStartTdd = new ArrayList<>(5);
        corrFUsuallyiTdd = new ArrayList<>(5);
        
        //Ya no se usan en la ultima version se reemplazo por los Map de CorrF (Arriba)
        //Añade el CorrF segun A1c a las lista correspondientes de TDD, Starting TDD y iTDD.
        corrFUsuallyTdd.add(corrFUsually(tdd, CorrFSegunA1c.MAYORQ10));
        corrFUsuallyTdd.add(corrFUsually(tdd, CorrFSegunA1c.OCHOYDIEZ));
        corrFUsuallyTdd.add(corrFUsually(tdd, CorrFSegunA1c.SIETEYOCHO));
        corrFUsuallyTdd.add(corrFUsually(tdd, CorrFSegunA1c.SEISSEISYSIETE));
        corrFUsuallyTdd.add(corrFUsually(tdd, CorrFSegunA1c.MENORQSEISSEIS));
        
        corrFUsuallyStartTdd.add(corrFUsually(startTdd, CorrFSegunA1c.MAYORQ10));
        corrFUsuallyStartTdd.add(corrFUsually(startTdd, CorrFSegunA1c.OCHOYDIEZ));
        corrFUsuallyStartTdd.add(corrFUsually(startTdd, CorrFSegunA1c.SIETEYOCHO));
        corrFUsuallyStartTdd.add(corrFUsually(startTdd, CorrFSegunA1c.SEISSEISYSIETE));
        corrFUsuallyStartTdd.add(corrFUsually(startTdd, CorrFSegunA1c.MENORQSEISSEIS));
        
        corrFUsuallyiTdd.add(corrFUsually(iTdd, CorrFSegunA1c.MAYORQ10));
        corrFUsuallyiTdd.add(corrFUsually(iTdd, CorrFSegunA1c.OCHOYDIEZ));
        corrFUsuallyiTdd.add(corrFUsually(iTdd, CorrFSegunA1c.SIETEYOCHO));
        corrFUsuallyiTdd.add(corrFUsually(iTdd, CorrFSegunA1c.SEISSEISYSIETE));
        corrFUsuallyiTdd.add(corrFUsually(iTdd, CorrFSegunA1c.MENORQSEISSEIS));
        */
    }
    
    //metodo que calcula la tasa basal segun porcentaje basal de la Tdd especificado en enum AvgBasalRates
    private BigDecimal avgBasalRateHourForTDD(BigDecimal tdd, AvgBasalRates rate){
        return tdd.multiply(rate.tasaBasal()).setScale(3, RoundingMode.HALF_EVEN);
    }
    
    //metodo que calcula los valores CorrF (List) para cada valor A1c especificados en enum CorrFSegunA1c
    private List<Double> corrF(BigDecimal tdd, CorrFSegunA1c formula){
        List<Double> list = new ArrayList<>(); //lista donde se almacena los valores CorrF recomendados
        
        list.add(formula.corrFRegla_1().divide(tdd, 1, RoundingMode.HALF_EVEN).setScale(1, RoundingMode.HALF_EVEN).doubleValue());
        if(formula.corrFRegla_2()!= null) //corrobora que la formula corrF posea dos valores Ej: 2400-2200, de lo contrario omite el calculo de CorrF con el segundo valor
            list.add(formula.corrFRegla_2().divide(tdd, 1, RoundingMode.HALF_EVEN).setScale(1, RoundingMode.HALF_EVEN).doubleValue());
        
        return list;
    }
    /*
    //Se uso en version 2.
    //metodo que calcula el corrF segun valor A1c reciente (Se reemplazó por el metodo de arriba para calcular el rango de CorrF recomendados según % A1c
    private Double corrFUsually(BigDecimal tdd, CorrFSegunA1c formula){
        Double d = formula.corrFRegla_1().divide(tdd, 1, RoundingMode.HALF_EVEN).setScale(1, RoundingMode.HALF_EVEN).doubleValue();
        //System.out.println("CORR > "+d);
        return d;//formula.corrFRegla().divide(tdd, 1, RoundingMode.HALF_EVEN).setScale(1, RoundingMode.HALF_EVEN).doubleValue();
    }
    */
    /*
    //Se uso en la 1ra version del programa
    @Override
    public String toString() {
        return String.format("%s%n %55s%n %45s%n %55s%n %-22s %33s%n %-22s %33s%n %17s %33s%n %17s %33s%n %17s %33s%n %17s %33s%n %17s %33s%n -%54s %n%n"
                + "%s%n %s%n %39s%n %s%n %13s %29s%n %14s %32s%n %13s %30s%n %10s %30s%n %11s %30s%n %10s %30s%n %13s %30s%n %s %n%n"
                + "%s%n %s%n %39s%n %s%n %24s %32s%n %s %33s%n %21s %33s%n %16s %39s%n %17s %30s%n %17s %33s%n %s %n%n",
                "Tabla 1: PROMEDIOS TASA BASAL SEGUN % BASAL DE TDD",
                "______________________________________________________", 
                "Avg. Basal Rate for Basal %s of TDD",                  
                "______________________________________________________",
                "For a basal % of:", "Avg. Basal Rate per Hour will be:",
                "_________________", "_________________________________",
                "40%", basalRatesForTdd.get(AvgBasalRates.CUARENTA) + " u/hr",
                "45%", basalRatesForTdd.get(AvgBasalRates.CUARENTAYCINCO) + " u/hr",
                "50%", basalRatesForTdd.get(AvgBasalRates.CINCUENTA) + " u/hr",
                "55%", basalRatesForTdd.get(AvgBasalRates.CINCUENTAYCINCO) + " u/hr",
                "60%", basalRatesForTdd.get(AvgBasalRates.SESENTA) + " u/hr",
                "------------------------------------------------------",
                "Tabla 2: FACTOR DE CORRECCIÓN SEGÚN A1C",
                "________________________________________________",
                "Use Larger CorrF with Lower A1c",
                "________________________________________________",
                "Recent A1c", "Corr Factor Formula",
                "________________", "_________________________",
                ">10%", corrFUsuallyTdd.get(0) + " grams/u",
                "8% to 10%", corrFUsuallyTdd.get(1) + " grams/u",
                "7% to 8%", corrFUsuallyTdd.get(2) + " grams/u",
                "6.6% to 7%", corrFUsuallyTdd.get(3) + " grams/u",
                "<6.6%", corrFUsuallyTdd.get(4) + " grams/u", 
                "------------------------------------------------------",
                "Tabla 3: TAMAÑO MODIFICACIONES AL FACTOR CARBOHIDRATOS",
                "______________________________________________________",
                "Change CarbF (ICR) in Small Steps",
                "______________________________________________________",
                "If current CarbF is:", "Adjust up or down by:",
                "________________________", "___________________________",
                "Less than 5.0 gr/u",  "+/- 0.2 to 0.3 grams/u", 
                "5-10 grams/u", "+/- 0.3 to 0.5 grams/u",
                "10-15 grams/u", "+/- 1.0 grams/u",
                "16-24 grams/u", "+/- 1 to 2 grams/u",
                "------------------------------------------------------");
    }
    */
    /*
    //Se uso en la 3ra version del programa
    public String toString2(){
        return String.format("%-41s%n"
                + "%65s %35s %31s%n"
                + "%-15s %40s %31s %29s%n"
                + "%15s %48s %31s %30s %n"
                + "%15s %48s %31s %30s %n"
                + "%15s %48s %31s %30s %n"
                + "%15s %48s %31s %30s %n"
                + "%15s %48s %31s %30s %n%n"
                + "%-40s%n"
                + "%65s %35s %31s%n"
                + "%15s %41s %32s %30s%n"
                + "%15s %44s %31s %27s %n"
                + "%15s %41s %31s %27s %n"
                + "%15s %42s %31s %27s %n"
                + "%15s %41s %31s %27s %n"
                + "%15s %44s %31s %27s %n%n"
                + "%s%n %s%n %39s%n %s%n %24s %32s%n %s %33s%n %21s %33s%n %16s %39s%n %17s %30s%n %17s %33s%n %s %n%n",
                "PROMEDIO TASA BASAL HORA (u/hr) SEGUN % BASAL DE TDD",
                "|Actual", "|Esperado", "|Mejorado",
                "% Basal de TDD", "Basal Hora", "Basal Hora", "Basal Hora",
                "40%", basalRatesForTdd.get(AvgBasalRates.CUARENTA) + " u/hr", basalRatesForStartTdd.get(AvgBasalRates.CUARENTA) + " u/hr", basalRatesForiTdd.get(AvgBasalRates.CUARENTA) + " u/hr",
                "45%", basalRatesForTdd.get(AvgBasalRates.CUARENTAYCINCO) + " u/hr", basalRatesForStartTdd.get(AvgBasalRates.CUARENTAYCINCO) + " u/hr", basalRatesForiTdd.get(AvgBasalRates.CUARENTAYCINCO) + " u/hr",
                "50%", basalRatesForTdd.get(AvgBasalRates.CINCUENTA) + " u/hr", basalRatesForStartTdd.get(AvgBasalRates.CINCUENTA) + " u/hr", basalRatesForiTdd.get(AvgBasalRates.CINCUENTA) + " u/hr",
                "55%", basalRatesForTdd.get(AvgBasalRates.CINCUENTAYCINCO) + " u/hr", basalRatesForStartTdd.get(AvgBasalRates.CINCUENTAYCINCO) + " u/hr", basalRatesForiTdd.get(AvgBasalRates.CINCUENTAYCINCO) + " u/hr",
                "60%", basalRatesForTdd.get(AvgBasalRates.SESENTA) + " u/hr", basalRatesForStartTdd.get(AvgBasalRates.SESENTA) + " u/hr", basalRatesForiTdd.get(AvgBasalRates.SESENTA) + " u/hr",
                "FACTOR DE CORRECCIÓN (grams/u) SEGÚN A1C", 
                 "|Actual", "|Esperado", "|Mejorado",
                 "A1c Reciente", "Corr Factor", "Corr Factor", "Corr Factor",
                 ">10%", corrFUsuallyTdd.get(0) + " grams/u", corrFUsuallyStartTdd.get(0) + " grams/u", corrFUsuallyiTdd.get(0) + " grams/u",
                 "8% to 10%", corrFUsuallyTdd.get(1) + " grams/u", corrFUsuallyStartTdd.get(1) + " grams/u", corrFUsuallyiTdd.get(1) + " grams/u",
                 "7% to 8%", corrFUsuallyTdd.get(2) + " grams/u", corrFUsuallyStartTdd.get(2) + " grams/u", corrFUsuallyiTdd.get(2) + " grams/u",
                 "6.6% to 7%", corrFUsuallyTdd.get(3) + " grams/u", corrFUsuallyStartTdd.get(3) + " grams/u", corrFUsuallyiTdd.get(3) + " grams/u",
                 "<6.6%", corrFUsuallyTdd.get(4) + " grams/u", corrFUsuallyStartTdd.get(4) + " grams/u", corrFUsuallyiTdd.get(4) + " grams/u",
                 "Tabla 3: TAMAÑO MODIFICACIONES AL FACTOR CARBOHIDRATOS",
                "______________________________________________________",
                "Change CarbF (ICR) in Small Steps",
                "______________________________________________________",
                "If current CarbF is:", "Adjust up or down by:",
                "________________________", "___________________________",
                "Less than 5.0 gr/u",  "+/- 0.2 to 0.3 grams/u", 
                "5-10 grams/u", "+/- 0.3 to 0.5 grams/u",
                "10-15 grams/u", "+/- 1.0 grams/u",
                "16-24 grams/u", "+/- 1 to 2 grams/u",
                "------------------------------------------------------"
                );
    }
    */
    //Se usa en la version actual del programa
    public String toString3(){
        return String.format("%s%n%n"
                + "%-41s%n" //tabla basal
                + "%31s %27s %25s%n" 
                + "%-14s %20s %25s %25s%n"
                + "%8s %26s %25s %25s %n"
                + "%8s %26s %25s %25s %n"
                + "%8s %26s %25s %25s %n"
                + "%8s %26s %25s %25s %n"
                + "%8s %26s %25s %25s %n%n"
                + "%s%n %-20s%70s%n %-20s%20s%n %-18s%70s%n%n%n" //tabla % basal de TDD
                + "%-40s%n" //tabla corrF
                + "%s %-30s %-30s %-30s%n" 
                + "%-20s %-30s %-30s %-30s%n"
                + "%-15s %30s %30s %30s %n"
                + "%-15s %30s %30s %30s %n"
                + "%-15s %30s %30s %30s %n"
                + "%-15s %30s %30s %30s %n"
                + "%-15s %30s %30s %30s %n%n"
                + "Tabla 2.1: %s%n" //9% TDD corrections
                + "%s%n%n%n" //tip correcciones
                + "%s%n%n %s%n%n" //tabla cambios basal
                + "%s%n %s%n %39s%n %s%n %24s %32s%n %s %33s%n %21s %33s%n %16s %39s%n %17s %30s%n %17s %33s%n %s %n%n", //tabla carbF Changes
                StringUtils.center("☺ [Valores sugeridos en base a su TDD ACTUAL de " + tdd + " u/día, TDD ESPERADO de " + startTdd + " u/día y TDD MEJORADO (iTDD) de " + iTdd + " u/día] ☺", 110),
                "Tabla 1: TASA BASAL HORA (u/hr) PROMEDIO SEGUN % BASAL DE TDD (Dosis Diaria Total)",
                "|ACTUAL", "|ESPERADA", "|MEJORADA",
                "% Basal de TDD", "Basal Hora", "Basal Hora", "Basal Hora",
                "40%", basalRatesForTdd.get(AvgBasalRates.CUARENTA) + " u/hr", basalRatesForStartTdd.get(AvgBasalRates.CUARENTA) + " u/hr", basalRatesForiTdd.get(AvgBasalRates.CUARENTA) + " u/hr",
                "45%", basalRatesForTdd.get(AvgBasalRates.CUARENTAYCINCO) + " u/hr", basalRatesForStartTdd.get(AvgBasalRates.CUARENTAYCINCO) + " u/hr", basalRatesForiTdd.get(AvgBasalRates.CUARENTAYCINCO) + " u/hr",
                "50%", basalRatesForTdd.get(AvgBasalRates.CINCUENTA) + " u/hr", basalRatesForStartTdd.get(AvgBasalRates.CINCUENTA) + " u/hr", basalRatesForiTdd.get(AvgBasalRates.CINCUENTA) + " u/hr",
                "55%", basalRatesForTdd.get(AvgBasalRates.CINCUENTAYCINCO) + " u/hr", basalRatesForStartTdd.get(AvgBasalRates.CINCUENTAYCINCO) + " u/hr", basalRatesForiTdd.get(AvgBasalRates.CINCUENTAYCINCO) + " u/hr",
                "60%", basalRatesForTdd.get(AvgBasalRates.SESENTA) + " u/hr", basalRatesForStartTdd.get(AvgBasalRates.SESENTA) + " u/hr", basalRatesForiTdd.get(AvgBasalRates.SESENTA) + " u/hr",
                "Tabla 1.1: PORCENTAGES DE BASAL USUAL DE TDD (Dosis Diaria Total) DE INSULINA", 
                "40-44%     »", "Niños y adultos que son sensibles a la insulina, fisicamente en forma, en una dieta alta en carbohidratos o tienen una producción residual de insulina en los primeros 5 años despues del diagnostico.",
                "45-60%     »", "La mayoria de la personas.",
                "60% o más  »", "Adultos o jovenes en una dieta baja en carbohidratos o quienes a menudo omiten bolos de carbohidratos.",
                "Tabla 2: RANGO FACTOR DE CORRECCIÓN (grams/u) PROMEDIO SEGÚN NIVEL HEMOGLOBINA GLICOSILADA (HbA1c)", 
                 StringUtils.rightPad("", 20, ' '), "|ACTUAL", "|ESPERADA", "|MEJORADA",
                 "A1c Reciente", "Corr Factor", "Corr Factor", "Corr Factor",
                 "<6.6%", corrFTdd.get(CorrFSegunA1c.MENORQSEISSEIS).get(0) + " - " + corrFTdd.get(CorrFSegunA1c.MENORQSEISSEIS).get(1) + " mg/dl per unit", corrFStartTdd.get(CorrFSegunA1c.MENORQSEISSEIS).get(0) + " - " + corrFStartTdd.get(CorrFSegunA1c.MENORQSEISSEIS).get(1) + " mg/dl per unit", corrFiTdd.get(CorrFSegunA1c.MENORQSEISSEIS).get(0) + " - " + corrFiTdd.get(CorrFSegunA1c.MENORQSEISSEIS).get(1) +  " mg/dl per unit",
                 "6.6% to 7%", corrFTdd.get(CorrFSegunA1c.SEISSEISYSIETE).get(0) + " - " + corrFTdd.get(CorrFSegunA1c.SEISSEISYSIETE).get(1) + " mg/dl per unit", corrFStartTdd.get(CorrFSegunA1c.SEISSEISYSIETE).get(0) + " - " + corrFStartTdd.get(CorrFSegunA1c.SEISSEISYSIETE).get(1) + " mg/dl per unit", corrFiTdd.get(CorrFSegunA1c.SEISSEISYSIETE).get(0) + " - " + corrFiTdd.get(CorrFSegunA1c.SEISSEISYSIETE).get(1) + " mg/dl per unit",
                 "7% to 8%", corrFTdd.get(CorrFSegunA1c.SIETEYOCHO).get(0) + " - " + corrFTdd.get(CorrFSegunA1c.SIETEYOCHO).get(1) + " mg/dl per unit", corrFStartTdd.get(CorrFSegunA1c.SIETEYOCHO).get(0) + " - " + corrFStartTdd.get(CorrFSegunA1c.SIETEYOCHO).get(1) + " mg/dl per unit", corrFiTdd.get(CorrFSegunA1c.SIETEYOCHO).get(0) + " - " + corrFiTdd.get(CorrFSegunA1c.SIETEYOCHO).get(1) + " mg/dl per unit",
                 "8% to 10%", corrFTdd.get(CorrFSegunA1c.OCHOYDIEZ).get(0) + " - " + corrFTdd.get(CorrFSegunA1c.OCHOYDIEZ).get(1) + " mg/dl per unit", corrFStartTdd.get(CorrFSegunA1c.OCHOYDIEZ).get(0) + " - " + corrFStartTdd.get(CorrFSegunA1c.OCHOYDIEZ).get(1) + " mg/dl per unit", corrFiTdd.get(CorrFSegunA1c.OCHOYDIEZ).get(0) + " - " + corrFiTdd.get(CorrFSegunA1c.OCHOYDIEZ).get(1) +  " mg/dl per unit",
                 ">10%", corrFTdd.get(CorrFSegunA1c.MAYORQ10).get(0) + " mg/dl per unit", corrFStartTdd.get(CorrFSegunA1c.MAYORQ10).get(0) + " mg/dl per unit", corrFiTdd.get(CorrFSegunA1c.MAYORQ10).get(0) + " mg/dl per unit",
                /*correctionsTdd(iTdd)*/correctionsTddFull(),
                "TIP: Una buena regla general es que si una glucosa aproximadamente 2 horas después de una comida aumenta menos de 40 mg/dL, un bolo de corrección adicional puede no ser necesario",
                "Tabla 3: TAMAÑO MODIFICACIÓN A TASA BASAL (u/hr) RECOMENDADOS CON BASE A TDD y CAIDA Ó AUMENTO DE GLUCOSA",
                changeBasal(), //llamada a metodo realiza calculo y muestra la tabla 3.
                "Tabla 4: TAMAÑO MODIFICACIONES AL FACTOR CARBOHIDRATOS (RATIO) HACIA ARRIBA ANTE HIPO O HACIA ABAJO ANTE HIPER",
                "______________________________________________________",
                "Change CarbF (ICR) in Small Steps",
                "______________________________________________________",
                "If current CarbF is:", "Adjust up or down by:",
                "________________________", "___________________________",
                "Less than 5.0 gr/u",  "+/- 0.2 to 0.3 grams/u", 
                "5-10 grams/u", "+/- 0.3 to 0.5 grams/u",
                "10-15 grams/u", "+/- 1.0 grams/u",
                "16-24 grams/u", "+/- 1 to 2 grams/u",
                "------------------------------------------------------"
                );
    }
    
    //metodo que imprime la tabla de modificaciones a tasa basal según subida o caida de glucosa con base al iTDD calculado
     private String changeBasal(){
         //List de enum con modificacion a tasa basal en (u/hr) segun variación glucemica de 20 a 80 mg/dl con base en TDD
        List<ChooseCheckChangeBasalRatesEnum> checkBasal = Arrays.asList(
                ChooseCheckChangeBasalRatesEnum.TDD_20U, ChooseCheckChangeBasalRatesEnum.TDD_30U, 
                ChooseCheckChangeBasalRatesEnum.TDD_40U, ChooseCheckChangeBasalRatesEnum.TDD_50U, 
                 ChooseCheckChangeBasalRatesEnum.TDD_60U, ChooseCheckChangeBasalRatesEnum.TDD_80U, 
                 ChooseCheckChangeBasalRatesEnum.TDD_100U);
        
        ListIterator<ChooseCheckChangeBasalRatesEnum> ite = checkBasal.listIterator(); //Iterador tipo List para tener opciones más personalizables como previous(), etc...
        ChooseCheckChangeBasalRatesEnum basalchangeChoise = checkBasal.get(0); //valor por defecto es para TDD de 20u 
        ChooseCheckChangeBasalRatesEnum basalchangeChoiseNxt = null;
        
        while(ite.hasNext()){
            basalchangeChoise = ite.next(); //Recorre lista de enum
            double basalChangeCurr = iTdd.doubleValue() - basalchangeChoise.getTdd(); //Obtiene diferecnia entre Tdd y el valor actaul de la lista Enum
            //Obtiene elemento enum ChooseCheckChangeBasalRatesEnum mas proximo al iTDD calculado
            if(basalChangeCurr<10){
                if(ite.hasNext()){
                    basalchangeChoiseNxt = ite.next(); 
                    double basalChangeNxt = basalchangeChoiseNxt.getTdd() - iTdd.doubleValue();  //Obtiene diferecnia entre  valor proximo Enum y Tdd
                    if(basalChangeCurr<basalChangeNxt) //si Tdd del usuario es más proximo al Tdd del Enum actual
                        break; //sale por que ya basalChangeChoise tiene el valor del Enum actual
                    else{
                        basalchangeChoise = basalchangeChoiseNxt; //almacena Enum next que corrspodne al Tdd más cercano al del usuario
                        break; //sale
                    }
                }   
                else
                    break; //sale si no tiene elementos la lista Enum con que comparar el Tdd
            }
        }
         System.out.println("Enum Basal Rates Change: "+basalchangeChoise);
        return String.format("%-17s %8s %8s %8s %8s %8s %8s %8s%n"
                + "%s %8s %8s %8s %8s %8s %8s %8s%n%n"
                + "%-17s %s%n%n"
                + "%s %8s %8s %8s %8s %8s %8s %8s%n"
                + "%s %8s %8s %8s %8s %8s %8s %8s%n", 
                 "1. With a Fall or", "20", "30", "40", "50", "60", "70", "80",
                  StringUtils.left("Rise in glucose of:", 20), "mg/dl", "mg/dl", "mg/dl", "mg/dl", "mg/dl", "mg/dl", "mg/dl",
                  "2. And this TDD:", StringUtils.center("3. LOWER or RAISE the basal rate for 8 hours by:", 56),
        StringUtils.center(basalchangeChoise.getTdd()+" U", 20) , basalchangeChoise.getLowerOrRaiseBasal().get(20), basalchangeChoise.getLowerOrRaiseBasal().get(30),
        basalchangeChoise.getLowerOrRaiseBasal().get(40), basalchangeChoise.getLowerOrRaiseBasal().get(50), basalchangeChoise.getLowerOrRaiseBasal().get(60),
        basalchangeChoise.getLowerOrRaiseBasal().get(70), basalchangeChoise.getLowerOrRaiseBasal().get(80), 
        StringUtils.center("", 20, ""), "u/hr", "u/hr", "u/hr", "u/hr", "u/hr", "u/hr", "u/hr");
    }
     
     //Metodo que calcula cantidad de dosis de bolos de correcciones optimos (6%, 9% y 11% del TDD) segun iTdd calculado (No se usa en la ultima versión)
     private String correctionsTdd(BigDecimal tdd){
         double corrF9PercentTdd = tdd.multiply(BigDecimal.valueOf(0.09)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
         double corrF6PercentTdd = tdd.multiply(BigDecimal.valueOf(0.06)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
         double corrF11PercentTdd = tdd.multiply(BigDecimal.valueOf(0.11)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
        return String.format("%s%n%n%s", StringUtils.left("UN INDICADOR DE UN ADECUADO CONTROL GLUCEMICO ES MANTENER LA SUMATORIA TOTAL DE BOLOS DE CORRECCION POR DEBAJO DEL 9% DE LA TDD (Dosis Diaria Total) AL DÍA:", 120), 
                StringUtils.left("De acuerdo a su iTDD de " +iTdd + " y con un buen control de la glucosa, sus bolos correctores deberian mantenersen por día entre "
                        + corrF6PercentTdd + " y " + corrF11PercentTdd + " u/día correspondiente al 6% y 11% de la TDD respectivamente"
                                + "e idealmente por debajo de " + corrF9PercentTdd + " u/día correspondiente al 9% de su TDD actual.", 270));
    }
     
     //Muestra los valores adecuados de bolo de correción para tdd actual, esperado y mejorado.
     private String correctionsTddFull(){
         double corrF9PercentTdd = tdd.multiply(BigDecimal.valueOf(0.09)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
         double corrF6PercentTdd = tdd.multiply(BigDecimal.valueOf(0.06)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
         double corrF11PercentTdd = tdd.multiply(BigDecimal.valueOf(0.11)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
         double corrF9PercentStartTdd = startTdd.multiply(BigDecimal.valueOf(0.09)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
         double corrF6PercentStartTdd = startTdd.multiply(BigDecimal.valueOf(0.06)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
         double corrF11PercentStartTdd = startTdd.multiply(BigDecimal.valueOf(0.11)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
         double corrF9PercentiTdd = iTdd.multiply(BigDecimal.valueOf(0.09)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
         double corrF6PercentiTdd = iTdd.multiply(BigDecimal.valueOf(0.06)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
         double corrF11PercentiTdd = iTdd.multiply(BigDecimal.valueOf(0.11)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
        return String.format("%s%n%n"
                + "%s %25s %25s %25s%n"
                + "%12s %25s %25s %25s %n"
                + "%12s %25s %25s %25s %n"
                + "%12s %25s %25s %25s %n", 
                StringUtils.left("UN INDICADOR DE UN ADECUADO CONTROL GLUCEMICO ES MANTENER LA SUMATORIA DE BOLOS DE CORRECCIÓN POR DEBAJO DEL 9% DE LA TDD (Dosis Diaria Total) AL DÍA", 150), 
                StringUtils.rightPad("", 12, ""), "|ACTUAL", "|ESPERADA", "|MEJORADA",
                 "% CORRECCION", "Bolo Corr Día", "Bolo Corr Día", "Bolo Corr Día",
                 "6% to 11%", corrF6PercentTdd + "-" + corrF11PercentTdd + " u/día", corrF6PercentStartTdd + "-" + corrF11PercentStartTdd + " u/día", corrF6PercentiTdd + "-" + corrF11PercentiTdd + " u/día",
                "<= 9%", corrF9PercentTdd + " u/día", corrF9PercentStartTdd + " u/día", corrF9PercentiTdd + " u/día"
                /* StringUtils.left("De acuerdo a su iTDD de " +iTdd + " y con un buen control de la glucosa, sus bolos correctores deberian mantenersen por día entre "
                        + corrF6PercentTdd + " y " + corrF11PercentTdd + " u/día correspondiente al 6% y 11% de la TDD respectivamente"
                                + "e idealmente por debajo de " + corrF9PercentTdd + " u/día correspondiente al 9% de su TDD actual.", 270)*/);
    }
}

//ENUM's usados en la clase
//enum de los datos necesarios para tasa dosis basal segun % basal de la TDD
enum AvgBasalRates {
    CUARENTA(0.40, new BigDecimal(0.017)),
    CUARENTAYCINCO(0.45, new BigDecimal(0.019)),
    CINCUENTA(0.50, new BigDecimal(0.021)),
    CINCUENTAYCINCO(0.55, new BigDecimal(0.023)),
    SESENTA(0.60, new BigDecimal(0.025)),;
    
    private final double porcentajeBasal;
    private final BigDecimal tasa;

    private AvgBasalRates(double porcentajeBasal, BigDecimal tasa) {
        this.porcentajeBasal = porcentajeBasal;
        this.tasa = tasa;
    }

    public double PorcentajeBasal() {
        return porcentajeBasal;
    }

    public BigDecimal tasaBasal() {
        return tasa;
    }
}

//enum de las formulas para calcular los diversos corrF según A1c reciente
enum CorrFSegunA1c{                         // A1c  | GS promedio | Use this Corr Factor Formula for mg/dL
    MAYORQ10(new BigDecimal("1450"), null),       // >10% | > 240 mg/dL | CorrF = 1450/TDD
    OCHOYDIEZ(new BigDecimal("1700"), new BigDecimal("1500") ),      // 8% to 10% | 183 to 240 mg/dL | = 1700 to 1500/TDD
    SIETEYOCHO(new BigDecimal("1900"), new BigDecimal("1800")),     // 7% to 8% | 154 to 183 mg/dL | = 1900 to 1800/TDD
    SEISSEISYSIETE(new BigDecimal("2100"), new BigDecimal("2000")), // 6.6% to 7% | 143 to 154 mg/dL | = 2100 to 2000/TDD
    MENORQSEISSEIS(new BigDecimal("2400"), new BigDecimal("2200")); // < 6.6% | < 143 mg/dL | = 2400 to 2200/TDD
    
    private final BigDecimal corrFRegla_1;
    private final BigDecimal corrFRegla_2;
    
    CorrFSegunA1c(BigDecimal corrFRegla_1, BigDecimal corrFRegla_2){
        this.corrFRegla_1 = corrFRegla_1;
        this.corrFRegla_2= corrFRegla_2;
    }
    
    public BigDecimal corrFRegla_1(){
        return corrFRegla_1;
    }
    
    public BigDecimal corrFRegla_2(){
        return corrFRegla_2;
    }
}