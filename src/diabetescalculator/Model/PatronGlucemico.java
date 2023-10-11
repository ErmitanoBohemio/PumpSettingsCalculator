/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package diabetescalculator.Model;

/**
 *
 * @author OscarFabianHP
 */
//enum de patrones glucemicos

public enum PatronGlucemico {
    MUY_BAJO("Inf. a 50 mg/dl", "Hipoglicemias severas por debajo de 50 mg/dl"),
    BAJO("Entre 55 y 65 mg/dl", "Hipoglicemias leves entre 65 mg/dl y 55 mg/dl"),
    BAJO_ALTO("Oscila entre 40 y 400 mg/dl", "Hipoglicemias e Hiperglicemias frecuentes que oscilasn entre 40 y 400 mg/dl"),
    ALTO("A1c > 7.5%", "A1c superior a 7.5% o glucosa promedio superior a 160 mg/gl con hipoglicemias poco frecuentes"),
    NORMAL("Entre 70 y 180 mg/dl", "Adecuado control glucemico con hipo e hiper glicemias esporádicas");
    
    private String patron;
    private String descripcion;
    
    private PatronGlucemico(String patron, String descripcion){
        this.patron = patron;
        this.descripcion=descripcion;
    }

    public String getPatron() {
        return patron;
    }

    public String getDescripcion() {
        return descripcion;
    }
    
    
}
