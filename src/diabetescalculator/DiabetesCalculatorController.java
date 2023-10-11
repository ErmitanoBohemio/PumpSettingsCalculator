/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package diabetescalculator;

import diabetescalculator.Model.BasalRatesRecommended;
import diabetescalculator.Model.ImpactYourTdd;
import diabetescalculator.Model.PatronGlucemico;
import diabetescalculator.Model.PersonPumpSettings;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Formatter;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

/**
 * FXML Controller class
 *
 * @author OscarFabianHP
 */
public class DiabetesCalculatorController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private TextField nombreTextField;
    @FXML
    private TextField pesoTextField;
    @FXML
    private TextField ddtTextField;
    @FXML
    private TextField gsPromedioTextField;
    @FXML
    private TextField gsObjectivoTextField;
    @FXML
    private ToggleButton hipoEHiperTButton;
    @FXML
    private ToggleButton hipoTButton;
    @FXML
    private ToggleButton hiperTButton;
    @FXML 
    private TextArea resultadoTextArea;
    @FXML
    private Button guardarButton;
    @FXML
    private Button calcularButton;
    @FXML
    private Button reiniciarButton;
    @FXML
    private ComboBox hipoTipoCombox;
    @FXML
    private TextField a1cTextField;
    @FXML
    private TextField gsCalculado;
    @FXML
    private ToggleGroup patronTGroup;
    @FXML
    private Label stateCalculatorLabel;
    @FXML
    private VBox vBox;
    
    //Variables de instancia 
    
    //Lista observable para detectar el patron seleccinado por usuario en el ComboBox al que se añade la lista
    private final  ObservableList<PatronGlucemico> tiposHiposOList = FXCollections.observableArrayList(PatronGlucemico.BAJO, PatronGlucemico.MUY_BAJO);
    
    //private static ObservableMap<Integer, Boolean> perfilGlucemico = FXCollections.observableHashMap();
    
    private PatronGlucemico patronSeleccionado = PatronGlucemico.NORMAL; //Por Defecto es NORMAL
    
    private final DecimalFormat df = new DecimalFormat("###.#");
    
    private Stage stage; //se usa para poder hacer uso de FileChooser en la operacion Guardar
    
    private StringBuilder resultadoApp; //Contiene el resultado una vez presiona el botón "calcular"
    
    private final PseudoClass errorClass = PseudoClass.getPseudoClass("error"); //suedo clase del css Style, usada para señalar un error en campo de texto
    private final Character[] caracteresReservados = new Character[]{'<', '>', ':', '/', '\\', '\"', '?', '*', '|', '\''} ;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       patronTGroup = new ToggleGroup(); //inicializo el ToggleGroup
       
        hipoTipoCombox.setItems(tiposHiposOList); //pongo en el Combox los elementos del ArrayList observable.
        
        //crea el listener que se ejecuta cuando se selecciona alguna de las opciones del ComboBox
        hipoTipoCombox.valueProperty().addListener(new ChangeListener<PatronGlucemico>() {
            @Override
            public void changed(ObservableValue ov, PatronGlucemico valueOld, PatronGlucemico valueNew) {
                if(valueNew != null){
                    patronSeleccionado=valueNew; //actualiza la variable con patron seleccionado en el ComboBox
                    stateCalculatorLabel.setText(""); //limpia el campo state ya que ha seleccionado una opcion requerida para el tipo de Hipos que presenta
                    hipoTButton.setUserData(patronSeleccionado); //Al seleccionar una opcion del ComboBox se pone la opcion seleccionada en el Hipo ToggleButton para que se ejecute la opción correspondiente entre BAJO o MUY_BAJO
                    System.out.println("Patron Hipo="+patronSeleccionado);
                }
            }
        });
        
        //Para cada ToggleButton se establece el dato de usuario correspodiente a la seleccion que realize el usuario según corresponda
        hipoTButton.setUserData(patronSeleccionado); //Este se actualiza segun el patron selecccionado del ComboBox Bajo o Muy_Bajo
        hipoEHiperTButton.setUserData(PatronGlucemico.BAJO_ALTO);
        hiperTButton.setUserData(PatronGlucemico.ALTO);
        
        //Agrupo instancias de ToggleButton en un ToggleGroup
        hipoTButton.setToggleGroup(patronTGroup);
        hipoEHiperTButton.setToggleGroup(patronTGroup);
        hiperTButton.setToggleGroup(patronTGroup);
        
        //listeners al ToggleButton de Hipoglicemias
        hipoTButton.selectedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable o) {
                if(hipoTButton.isSelected() && !hipoTipoCombox.isPressed()) {//Verifica que solo muestre el ComboBox solo si esta seleccionado el ToggleButton de Hipoglicemias en este caso
                    hipoTipoCombox.setVisible(true); //muestra el ComboBox con la lista de opciones 
                    //muestra visualmente la seleccion del usuario
                    hipoTButton.setText("Si"); 
                    hipoEHiperTButton.setText("No");
                    hiperTButton.setText("No");
                    stateCalculatorLabel.setText("Elija una opción de Hipoglicemias que presenta más frecuentemente");
                }
                else {//no esta seleccionado el ToggleButton de Hipos
                    hipoTipoCombox.setVisible(false);
                    stateCalculatorLabel.setText(""); //limpia texto recordatorio selección tipo hipos
                    hipoTipoCombox.setValue(null); //reincio Combo box para que pida seleccionar alguna opcion
                    hipoTipoCombox.setPromptText("Selecccione tipo de hipoglicemias más frecuentes");
                }
            }
        });
        //listeners al ToggleButton de Hipo e Hiperglicemias
        hipoEHiperTButton.selectedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable o) {
                    hipoEHiperTButton.setText("Si");
                    hipoTButton.setText("No");
                    hiperTButton.setText("No");
            }
        });
        //listeners al ToggleButton de Hiperglicemias
        hiperTButton.selectedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable o) {
                    hiperTButton.setText("Si");
                    hipoTButton.setText("No");
                    hipoEHiperTButton.setText("No");
            }
        });
        
        //Listeners y EventHandler COMUNES que comprueba que solo se ingrese numeros en los campos que corresponda
        pesoTextField.textProperty().addListener(((ov, od, nw) -> {
            if(!nw.matches("\\d*\\.?\\d*$")){
               //The test case attempts to modify the text property inside the change handler. This is not supported, and 
               //the property event handlers are not re-entrant. The correct way is for the application to delay the 
               //modification, using runLater(): 
               pesoTextField.pseudoClassStateChanged(errorClass, true); //señala campo origen del error
                Platform.runLater(()->{
                    pesoTextField.clear();
                    stateCalculatorLabel.setText("Entrada ( " + nw + " ) no valida, solo numeros Ej: 78 ó 67.8 ");
                });
                
            }
            else{
                stateCalculatorLabel.setText(""); //señala campo origen del error
                pesoTextField.pseudoClassStateChanged(errorClass, false);   
            }
        }));
        
        pesoTextField.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>(){
           @Override
           public void handle(KeyEvent event) {
               if(!Character.isDigit(event.getCharacter().charAt(0)) && event.getCharacter().charAt(0)!='.')//Ignorar todo lo que no sea un digito (0...9) ó '.'
                   event.consume();
               if(event.getCharacter().charAt(0)=='.' && pesoTextField.getText().contains(".")) //ha tecleado un '.' y ya se ha tecleado antes un '.', entonces ignorar
                   event.consume(); //ignorar
               if(pesoTextField.getText().length()>4 && pesoTextField.getText().contains(".")) //si tiene formato ###.# ignorar culquier otra entradada
                   event.consume();
               if(pesoTextField.getText().length()>2  && !pesoTextField.getText().contains(".") && event.getCharacter().charAt(0)!='.') //Si ya tiene contiene formato ### ignora cualquier caracter distinto al '.'
                   event.consume();
           }
        });
        
        //Event Handlers and Listeners
        nombreTextField.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>(){
           Character[] caracteresPermitidos = new Character[]{'-', '_', '\s', '.', '(', ')', '[', ']'} ;
           @Override
           public void handle(KeyEvent event) {
               char charTyped = event.getCharacter().charAt(0);
               if(!Character.isLetterOrDigit(event.getCharacter().charAt(0)) && charTyped!=' ' && charTyped!='-' && charTyped!='_'
                       && charTyped!='.' && charTyped!='(' && charTyped!=')' && charTyped!='[' && charTyped!=']')
                   event.consume(); //ignorar escritura
               for(char c : caracteresReservados) 
                    if(event.getCharacter().charAt(0)== c)//Si ha tecleado alguno de los caracteres reservados 
                        event.consume(); //ignorar escritura
        }});
        
        
        //Listeners of the inner class with TextField (Clase interna private definida por mi más abajo Llamada TextFieldListener
        //ddtTextField.textProperty().addListener(new TextFieldListener(ddtTextField));
        ddtTextField.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>(){
           @Override
           public void handle(KeyEvent event) {
               if(!Character.isDigit(event.getCharacter().charAt(0)) && event.getCharacter().charAt(0)!='.') //Ignorar todo lo que no sea un digito (0...9) ó '.'
                   event.consume();
               if(event.getCharacter().charAt(0)=='.' && ddtTextField.getText().contains(".")) //Si se teclea '.' y ya se ingreso '.' antes, ignorar
                   event.consume();
               if(ddtTextField.getText().length()>4 && ddtTextField.getText().contains(".")) //si tiene formato ###.# ignorar culquier otra entradada
                   event.consume();
               if(ddtTextField.getText().length()>2  && !ddtTextField.getText().contains(".") && event.getCharacter().charAt(0)!='.') //Si ya tiene contiene formato ### ignora cualquier caracter distinto al '.'
                   event.consume();
           }
        });
        
        //Listener y EventHandler del campo GSPromedio
        gsPromedioTextField.textProperty().addListener(new GlucoseTextFieldListener(gsPromedioTextField));
        gsPromedioTextField.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>(){
           @Override
           public void handle(KeyEvent event) {
               if(gsPromedioTextField.getUserData()!=null){ //verifica si el campo tiene señalado un error, que se ha guardado en UserData del campo
                   Object o = gsPromedioTextField.getUserData();
                   stateCalculatorLabel.setText(o.toString()); 
               }
               if(!Character.isDigit(event.getCharacter().charAt(0)) ){ //ignorar si la entrada no es un digito 0...9
                   event.consume();
               }
               if(gsPromedioTextField.getText().length()>2) //Ignorar si el campo ya tiene los tres digitos maximos permitidos
                   event.consume();
               if(gsPromedioTextField.getText().isEmpty()) //si el campo esta vacio
                   stateCalculatorLabel.setText(""); //borre el texto escrito en la etiqueta state.
           }
           
        });
        gsPromedioTextField.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
           @Override
           public void handle(MouseEvent event) {
               //gsPromedioTextField.selectAll(); //selecciona el texto escrito en el campo
             if(gsPromedioTextField.getUserData()!=null){ //verifica si el campo tiene señalado un error, que se ha guardado en UserData del campo
                   Object o = gsPromedioTextField.getUserData();
                   stateCalculatorLabel.setText(o.toString()); 
               }   
           }
        });
        
        //Listener y EventHandler del campo GSObjetivo
        gsObjectivoTextField.textProperty().addListener(new GlucoseTextFieldListener(gsObjectivoTextField));
        gsObjectivoTextField.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>(){
           @Override
           public void handle(KeyEvent keyEvent) {
               if(gsObjectivoTextField.getUserData()!=null){ //verifica si el campo tiene señalado un error, que se ha guardado en UserData del campo
                   Object o = gsObjectivoTextField.getUserData();
                   stateCalculatorLabel.setText(o.toString());
               }
               //Revisa si ha tecleado (Typed) no es digito o no es punto (.)
               if(!Character.isDigit(keyEvent.getCharacter().charAt(0)) && keyEvent.getCharacter().charAt(0)!= '.'){
                   keyEvent.consume(); //ignorar lo typed (tecleado)x   
               }
               if(gsObjectivoTextField.getText().length()>2)//ignorar si ya el campo contiene tres digitos
                   keyEvent.consume();
               if(gsObjectivoTextField.getText().isEmpty()) //si el campo esta vacio
                   stateCalculatorLabel.setText(""); //borra cualquier advetencia en la etiqueta state
           }
            
        });
        gsObjectivoTextField.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
           @Override
           public void handle(MouseEvent event) {
               //gsObjectivoTextField.selectAll(); //selecciona el texto escrito en el campo
             if(gsObjectivoTextField.getUserData()!=null){ //verifica si el campo tiene señalado un error, que se ha guardado en UserData del campo
                   Object o = gsObjectivoTextField.getUserData();
                   stateCalculatorLabel.setText(o.toString()); 
               }   
           }
        });
        
        //Usando la Binding API
        /*
        The Bindings class in JavaFX is a utility class containing 249 methods for property binding. 
        It allows you to create bindings between observable objects of various types. You can combine 
        properties with values, such a Strings and numbers, depending on the binding.
        */
        
        //Creo Binding con la operacion de convertir A1C en eAG described by the formula 28.7 X A1C – 46.7 = eAG.
        /*StringBinding a1cToeag = Bindings.createStringBinding(new Callable<String>() {
                           @Override
                           public String call() throws Exception {
                               if(a1cTextField.getText().isEmpty())
                                   return "";
                               else 
                                   return String.valueOf(Math.round(28.7 * Double.valueOf(a1cTextField.textProperty().get()) - 46.7));
                           }
                        }, a1cTextField.textProperty());
        
        //Listener invalidation que se ejecuta cuando se modifica la propiedad texto de campo a1cTextField
        a1cTextField.textProperty().addListener(new InvalidationListener() {
           @Override
           public void invalidated(Observable o) {
                gsCalculado.setText(a1cToeag.get()); //obtiene el resultado del Binding a1cToeag y lo pone en el campo gsCalculado
           }
        });*/
        
        /*//Asi seria si tambien quisiera que por medio de un RadioButton se pudiera elegir de convertir de A1c to eAG y viceversa.
        StringBinding eagToa1c = Bindings.createStringBinding(() -> {
            if(gsCalculado.getText().isEmpty())
                return "";
            else
                return df.format((Integer.valueOf(gsCalculado.textProperty().get())+46.7)/28.7);
        }, gsCalculado.textProperty());
        
        gsCalculado.textProperty().addListener(new InvalidationListener() {
           @Override
           public void invalidated(Observable o) {
               a1cTextField.setText(eagToa1c.get()); //obtiene resultado de Binding eagToa1c y lo poner en campo de entreda del A1c
           }
        });*/
        
        
        
        //Otra forma de binding por medio del Fluent API
        a1cTextField.textProperty().addListener(new ChangeListener<String>() {
           @Override
           public void changed(ObservableValue<? extends String> ov, String valueOld, String valueNew) {
                   try{
                       if(valueNew.isEmpty() || valueNew == null || valueNew == ""){//si el campo A1c esta vacio
                           //Se usa para evitar la IlegalArgumentException al modificar la propiedad (a1cTextField) dentro de mismo listener
                           Platform.runLater(() -> {
                               //a1cTextField.clear(); //no se necesita por que sino no deja poner numeros de entre 10 y 12 %
                               gsCalculado.clear();
                               gsCalculado.textProperty().unbind(); //se unbind para poser usar metodos set en ese campo bind -> gsCalculado
                               gsCalculado.setText("");
                                });
                       }   
                       else /*if(valueNew.matches("\\d*\\.?\\d*$") ){*/ //Si valor ingresado es numerico (Este if ya no se necesita por que el campo tiene un EventHandler KeyTyped que solo permite escribit digitos con un formato especifico.
                           if(Double.valueOf(valueNew)>=5.0 && Double.valueOf(valueNew)<=12.99){ //Si ingresa valor valido A1c (5.0-13.0%)
                       
                                stateCalculatorLabel.setText(""); //limpia campo state de advertencias
                                //Aqui se hace un Binding mediante Fluent API
                                
                                DoubleProperty a1cUser=new SimpleDoubleProperty(Double.valueOf(valueNew)); //property con el valor A1c ingresado
                                //The Fluent API relies on the creation of “expression” objects, which are similar to properties 
                                //(they’re observable values) with extra convenience methods to support extra manipulation.
                                DoubleExpression gsAvgA1c = a1cUser.multiply(28.7).subtract(46.7);  //Expression q Realiza calculo de conversion de A1c a "Glucosa promedio"
                                gsCalculado.textProperty().bind(gsAvgA1c.asString("%.0f")); //enlaza (Bind)Text Property de gsCalculado con la Expression gsAvgA1c
                                gsAvgA1c.get(); //realiza conversion enlazada (Binding) al campo de texto de A1c y la Expression (gsAvgA1c) de la conversion segun entrada % A1c
                                gsPromedioTextField.setText(gsAvgA1c.asString("%.0f").get()); //Copia el valor de Glucosa en sangre promedio calculado a partir del dato de HB1Ac con la calculadora en el campo Glucosa en Sangre Promedio
                           }
                            else{ //Si no ingresa valor A1c valido entre (5.0-12.0%)
                                    stateCalculatorLabel.setText("Ingrese valor A1c valido de entre 5.0 y 12.99 %");
                                    /*gsCalculado.textProperty().unbind(); //se unbind para poder usar metodos set en campo bind -> gsCalculado
                                    a1cTextField.setText("");
                                    gsCalculado.setText("");*/
                                    Platform.runLater(()->{
                                        //a1cTextField.clear(); //no se necesita por que sino no deja poner numeros de entre 10 y 12 %
                                        gsCalculado.clear();
                                        gsCalculado.textProperty().unbind(); //se desenlaza para poder usar operacion set
                                        gsCalculado.setText("");
                                    });
                                    if(gsPromedioTextField.getText()!= null || !gsPromedioTextField.getText().isEmpty() || !gsPromedioTextField.getText().isBlank() )
                                        gsPromedioTextField.setText("");
                                    }
                       }
                       //else //si el valor ingresado no es numerico (Este ultimo Else sobra ya que basta con Else de arriba)
                   //{
                       
                       /*
                       //EXPLICACIÓN  SOBRE COMO USAR PLATFORM RUNLATER PARA PODER MODIFICAR PORPERTY
                       No esta soportado modificar la propiedad dentro del Change Handler en este caso las Properties son el campo A1c y eAG, 
                       por lo que al modificar no se re-ingresa al event Handler por lo que lanza la excepcion (java.lang.IllegalArgumentException: The start must be <= the end)
                       para solucionarla se retardar la modificación con el uso del Platform.runLater(Runnable())
                       
                       "The test case attempts to modify the text property inside the change handler. This is not supported, 
                       and the property event handlers are not re-entrant. The correct way is for the application to delay 
                       the modification, using runLater():"
                        if (matcher.find()) { 
                            System.err.println("true"); 
                            Platform.runLater(() -> { 
                                textField.clear(); 
                            }); 
                        }
                       */

                       /*  //Se comento por que sobra este ultimo else, vasta con el else de arriba q muestra mensaje si valor A1c no esta entre 5.0 y 13.0
                            Platform.runLater(()->{
                                        //a1cTextField.clear(); //no se necesita por que sino no deja poner numeros de entre 10 y 12 %
                                        gsCalculado.clear();
                                        gsCalculado.textProperty().unbind(); //se desenlaza para usar operacion set
                                        gsCalculado.setText("");
                                    });
                       stateCalculatorLabel.setText("Entrada ( " + valueNew + " ) no valida, solo numeros entre 5.0 y 12.0 %" );
                   }
                }*/
                
                    catch(NumberFormatException ex){
                        System.err.println("Campo A1c empty");
                        }
                   catch(IllegalArgumentException ex){
                       System.err.println("Entrada ilegal en el campo");
                   }
               }
       });
        
        //Listener para detectar cuando el focus esta sobre el TextField gsCalculado
        gsCalculado.focusedProperty().addListener(new InvalidationListener() {
           @Override
           public void invalidated(Observable o) {
               if(guardarButton.isDisable() && (gsObjectivoTextField.getText().isEmpty() || gsObjectivoTextField.getText()==null || gsObjectivoTextField.getText()==""))
                    gsObjectivoTextField.requestFocus(); //al detectar focus sobre el campo gsCalculado tranfiere el focus al TextField gsObjetivo
           }
        });
        
        //EventHandler A1c TextField
        a1cTextField.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>(){
           @Override
           public void handle(KeyEvent event) {
               char charTyped = event.getCharacter().charAt(0);
               if(!Character.isDigit(charTyped) && charTyped != '.') //Si lo que se tecleo no es un Digito (numeros) o un "."
                   event.consume(); //ignore lo tecleado
               if(a1cTextField.getText().contains(".") && charTyped=='.') //Si el campo A1c ya contiene un "." y se tecleo otro "."
                   event.consume(); //ignore lo tecleado
              /*
               //se comento por que se reemplazaron mejor por las reglas de abajo 
               if(a1cTextField.getText().matches("\\d\\.\\d")) //si el campo A1c ya tiene escrito un numero de este estilo Ej: 5.8, 6.6, 7.0, etc...
                   event.consume(); //ignore lo tecleado
               if(a1cTextField.getText().matches("\\d{2}\\.\\d")) //si el campo A1c ya tiene escrito un numero de este estilo Ej: 10.0, 11.6, 12.0, etc...
                   event.consume(); //ignore lo tecleado*/
               if(a1cTextField.getText().length()>1 && !a1cTextField.getText().contains(".") && charTyped != '.') //Ignorar todo valor que no sea # o ##, es decir q Si lo escrito en el campo A1c tiene más dos digitos (##..), no cotiene ya un "." además lo tecleado no es un "."
                   event.consume(); //ignore lo tecleado
               if(a1cTextField.getText().length()>3 && a1cTextField.getText().contains(".") && !a1cTextField.getText().matches("\\d{2}\\..")) //Ignorar todo valor distinto de este formato #.## o ##.##
                   event.consume();
               
           }
            
        });
        
        //una forma
        //gsCalculado.textProperty().bind(gsEstimada.asString());
        
        //otra forma
        //gsCalculado.textProperty().bind(a1c.multiply(28.7).subtract(46.7).asString());
        
        /*
        //Perfil Glucemico por Defecto 
        perfilGlucemico.put(50, Boolean.FALSE);
        perfilGlucemico.put(65, Boolean.FALSE);
        perfilGlucemico.put(200, Boolean.FALSE);*/
    }  
    
    //metodo que maneja el evento de los ToggleButtons, almacena en la variable de instancia PatronGlucemicoel el patron correspondiente al ToggleButton seleccionado por el usurio
    @FXML
    private void patronToggleButtonSelected(ActionEvent event){
        if(patronTGroup.getSelectedToggle()!= null) //si alguno ToggleButton a sido Seleccionado (=! null)
        patronSeleccionado = (PatronGlucemico) patronTGroup.getSelectedToggle().getUserData(); //obtiene dato del ToggleButton seleccionado
        else{ //si el toggleButton fue deseleccionado es decir PatronGlucemico es de nuevo NORMAL
            patronSeleccionado = PatronGlucemico.NORMAL;  //establece el Patron Seleccionado NORMAL que es el estado cuando ningun ToggleButton es seleccioando.
            hipoTButton.setText("No");
            hiperTButton.setText("No");
            hipoEHiperTButton.setText("No");
        }
    }
    
    //Realiza el calculo del la configuracion de optima de la Bomba de insulina de acuerdo a datos ingresados
    @FXML
    private void calcularAction(ActionEvent event){
        try{
            stateCalculatorLabel.setText(""); //limpia cualquier mensaje en la etiqueta
            
            //Captura los datos requeridos para el calculo proporcionados por el usuario
            double peso = Double.valueOf(pesoTextField.getText());
            double tdd = Double.valueOf(ddtTextField.getText());
            double  gsProm = Double.valueOf(gsPromedioTextField.getText());
            double gsObj = Double.valueOf(gsObjectivoTextField.getText());
            PatronGlucemico patron = patronSeleccionado;
            
            if(!(gsProm>=80 && gsProm<=380) || !(gsObj>=100 && gsObj<=180)){
                stateCalculatorLabel.setText("Revizar valor de los campos en rojo");
                return;
            }
            
            if(hipoTButton.isSelected()) //verifica antes de hacer calculo si el ToggleButton de Hipoglicemias ha sido seleccionado
                if(patronSeleccionado == PatronGlucemico.NORMAL || patronSeleccionado.equals(PatronGlucemico.NORMAL) || hipoTipoCombox.getValue()==null ){ //y si se ha escogido una opción en el ComboBox de las tipos de HIPO
                   stateCalculatorLabel.setText("Elija una opción del tipo de Hipoglicemias que presenta");
                    System.out.println("Patron Selected: "+patronSeleccionado);
                   return;
                }
        
            //si el area del resultado tiene texto de calculo anterior lo borra antes de proceder a mostrar el resultado del nuevo calculo
            if(!resultadoTextArea.getText().isEmpty())
                resultadoTextArea.setText("");
            
        //imprime la configuración recomendada según datos ingresados
        PersonPumpSettings personiTDD = new PersonPumpSettings(peso, tdd, gsProm, gsObj, patron);
        BasalRatesRecommended recommendeds = new BasalRatesRecommended(personiTDD.getiTDD(), personiTDD.getDosisDiariaTotalPromedio(), personiTDD.getTddStarting());
        ImpactYourTdd impactGlucose = new ImpactYourTdd(personiTDD.getDosisDiariaTotalPromedio(), personiTDD.getPeso());
        guardarButton.setDisable(false);
        
        resultadoApp = new StringBuilder(); //instancio el StringBuilder que contendrá el resulado del calculo para ser luego exportado en archivo TXT (con formato) si así lo requiere el usuario
        resultadoApp.append(String.format("%s%n%n%s%n", "CONFIGURACIÓN RECOMENDADA CON BASE EN TDD ACTUAL, TDD ESPERADO Y TDD MEJORADO:", personiTDD.toString2())); //Realiza los calculos para TDD, StartingTDD y iTDD asi como de Basal, Ratio, Sensibilidad, etc...
        resultadoApp.append(String.format("%s%n%n", StringUtils.center(impactGlucose.toString(), 120))); //muestra el resultado de la tabla 9.8 Things That Impact Your TDD
        resultadoApp.append(String.format("%s%n%n%s%n%n", StringUtils.center("RANGOS USUALES PERSONALIZADOS PARA EL BUEN CONTROL", 85, "☻"), recommendeds.toString3())); //personiTDD.calcularTasasBasales().toString()));
        resultadoApp.append(String.format("RECOMEDACIONES:%n%n%s%n%s%n%n%n", 
            "Si su TDD inicial es óptimo se hace evidente en los primeros días en una bomba. Controle su glucosa y revise su "
            + "dispositivo de MCG con frecuencia. Si se producen niveles altos o bajos o glucosa errática, el TDD promedio en "
            + "una bomba se puede reajustar con nuevos ajustes seleccionados a partir de un nuevo TDD para comprobar basal y bolo, "
            + "o se puede usar el manejo de patrones para ajustar un ajuste específico si se observa un patrón. Una vez que el TDD "
            + "parezca apropiado, verifique y cambie Las tasas basales primero, luego el factor carbohidratos, seguido por el factor "
            + "de corrección.", 
            "La verificación generalmente se puede hacer en las primeras semanas idealmente de una a tres semanas después del arranque "
            + "de la bomba. Su objetivo es encontrar la configuración de la bomba que mantenga su glucosa estable con la mayoría de las lecturas en su rango objetivo."));
        resultadoApp.append(String.format("DEFINICIONES ÚTILES:%n%n%s%n%n%s%n%n%s%n%n%s%n%n%s%n%n%s%n%n%s%n%n%n", 
            "■ Basal Rates (Basal) = Una basal precisa es aquella que mantiene la glucosa dentro de +/- 20 mg/dL (1.1 mmol / L) de la glucosa inicial y durante las proximas 6 a 8 horas.",
            "■ Carb Factor (CarbF) = La proporción de insulina a carbohidratos o gramos de carbohidratos cubiertos por una unidad de insulina; Un CarbF preciso trae su glucosa dentro de 30 mg/dL (1.7 mmol/L) de su glucosa inicial después de 4 a 5 horas de un bolo.",
            "■ Correction Factor (CorrF) = Qué tanto es probable caiga su glucosa con cada unidad de insulina, a veces llamado factor de sensibilidad a la insulina; un Factor de corrección ideal es aquel que logra bajar una glucosa alta y traerla al valor objetivo de glucosa en un rango de ±30 mg/dl",
            "■ Rel. Ins. Sensitivity (IS) = Sensibilidad a la insulina se presenta en comparación con un promedio del 100%. (< 100% = menos sensible, > 100% = más sensible).",
            "■ Average Blood Glucose (Avg BG) = Glucosa en sangre promedio durante 14 días o más (se prefiere un promedio de 40 o más pruebas de BG).",
            "■ Total Daily Dose (TDD) = Insulina total utilizada por día (todas las dosis basales, de carbohidratos y de corrección al día).",
            "■ Improved TDD (iTDD) = la TDD mejorada es aquella que se ajusta hacia arriba cuando la glucosa promedio es demasiado alta o esta por encima de rango objetivo, o hacia abajo cuando presenta patrones hipoglucemicos y de este modo obtener una mejora en el control glucémico a través de una TDD más precisa."));
        resultadoApp.append(String.format("REGLAS PARA CAMBIOS EN BASAL Y BOLOS:%n%n%s%n%n%s%n%n%s%n%n%s%n%n%s%n%n%s%n%n%s%n%n%s%n%n", 
                "1. Cambiar una dosis a la vez.", 
                "2. Para detener las hipoglicemias frecuentes, la Basal disminuye y CarbF o CorrF aumentan creando un bolo más pequeño; Para detener las hiperglicemias frecuentes, la Basal aumenta y CarbF o CorrF disminuyen, creando un bolo más grande.",
                "3. Verifique su proporción basal/bolo de carbohidratos, usualmente en adultos y adolecentes la proporcion basal es del (45-60%) de la TDD. Los bajas frecuentes suelen ser causadas por el que es más grande, mientras que las altas frecuentes son causado por el que es más pequeño.",
                "4. Si sus lecturas son relativamente estables y cercanas a su rango objetivo, un pequeño cambio en sus índices basales o bolos debería ser suficiente. Los carbohidratos (CarbF) generalmente cambian en un punto o menos (Ver. Tabla 4), las correcciones (CorrF) de tres a diez puntos y tasas basales de 0,025 a 0,1 u/h pruebe cambios pequeños primero; pueden ser necesarios cambios más grandes para bajas o altas frecuentes.",
                "5. Cuando cambie la configuración de una bomba, determine cuánta más insulina este cambio le dará cada día (Su bomba ¡realmente debería estar haciendo esto por ti!).",
                "6. Ajuste los factores de carbohidratos (CarbF) y corrección (CorrF) cada 4 días y las tasas basales cada 7 días minimo hasta que su control mejore.",
                "7. Para detener las lecturas bajas o altas, cambie los índices basales al menos de 5 a 8 horas antes de que ocurra la lectura (Ver TABLA #3) y el CarbF o CorrF (Ver TABLA #2) antes de la comida previa.",
                "8. Para resolver un patrón no deseado, verifique dos veces su actual dosis diaria total (TDD) de insulina contra los ajustes óptimos recomendados por la App según su TDD promedio actual y sus ajustes actuales de la bomba o en también en www.opensourcediabetes.org."));
        
        //resultadoTextArea.appendText(String.format("%s%n%n%s", "CONFIGURACIÓN RECOMENDADA SEGÚN iTDD:", personiTDD.toString()));
        
        //resultadoTextArea.appendText(String.format("%s%n%n%s", "RANGOS USUALES PARA EL BUEN CONTROL:", personiTDD.calcularTasasBasales().toString()));
  
        //imprime lo que seria la configuración actual de la bomba según datos ingresado
        /*PersonPumpSettings personTDD = new PersonPumpSettings(peso, tdd, gsProm, gsObj, PatronGlucemico.NORMAL);
        resultadoTextArea.appendText("Configuración Actual según TDD proporcionado por usuario:\n\n" + personTDD.toString());
            System.out.println("Run calcular");*/
        
        /*resultadoTextArea.appendText(String.format("%nRECOMEDACIONES:%n%n%s%n%s%n%n", 
            "Si su TDD inicial es óptimo se hace evidente en los primeros días en una bomba. Controle su glucosa y revise su "
            + "dispositivo de MCG con frecuencia. Si se producen niveles altos o bajos o glucosa errática, el TDD promedio en "
            + "una bomba se puede reajustar con nuevos ajustes seleccionados a partir de un nuevo TDD para comprobar basal y bolo, "
            + "o se puede usar el manejo de patrones para ajustar un ajuste específico si se observa un patrón. Una vez que el TDD "
            + "parezca apropiado, verifique y cambie Las tasas basales primero, luego el factor carbohidratos, seguido por el factor "
            + "de corrección.", 
            "La verificación generalmente se puede hacer en las primeras semanas idealmente de una a tres semanas después del arranque "
            + "de la bomba. Su objetivo es encontrar la configuración de la bomba que mantenga su glucosa estable con la mayoría de las lecturas en su rango objetivo."));
            
        resultadoTextArea.appendText(String.format("%nDEFINICIONES ÚTILES:%n%n%s%n%n%s%n%n%s%n%n%s%n%n%s%n%n%s%n%n%s%n%n%n", 
            "Basal Rates (Basal) = Una basal precisa es aquel que mantiene la glucosa dentro de +/- 20 mg/dL (1.1 mmol / L) de la glucosa inicial y durante las proximas 6 a 8 horas.",
            "Carb Factor (CarbF) = La proporción de insulina a carbohidratos o gramos de carbohidratos cubiertos por una unidad de insulina; Un CarbF preciso trae su glucosa dentro de 30 mg/dL (1.7 mmol/L) de su glucosa inicial después de 4 a 5 horas de un bolo.",
            "Correction Factor (CorrF) = Qué tanto es probable caiga su glucosa con cada unidad de insulina, a veces llamado factor de sensibilidad a la insulina.",
            "Rel. Ins. Sensitivity (IS) = Sensibilidad a la insulina que presenta en comparación con un promedio del 100%. (< 100% = menos sensible, > 100% = más sensible).",
            "Average Blood Glucose (Avg BG) = Glucosa en sangre promedio durante 14 días o más (se prefiere un promedio de 40 o más pruebas de BG).",
            "Total Daily Dose (TDD) = Insulina total utilizada por día (todas las dosis basales, de carbohidratos y de corrección al día).",
            "Improved TDD (iTDD) = la TDD actual que se ajusta hacia arriba cuando la glucosa promedio es demasiado alta o esta por encima de rango objetivo, o hacia abajo cuando presenta patrones hipoglucemicos y de este modo obtener una TDD más precisa."));
        
        resultadoTextArea.appendText(String.format("REGLAS PARA CAMBIOS EN BASAL Y BOLOS:%n%n%s%n%n%s%n%n%s%n%n%s%n%n%s%n%n%s%n%n%s%n%n%s%n%n", 
                "1. Cambiar una dosis a la vez.", 
                "2. Para detener las hipoglicemias frecuentes, la Basal disminuye y CarbF o CorrF aumentan creando un bolo más pequeño; Para detener las hiperglicemias frecuentes, la Basal aumenta y CarbF o CorrF disminuyen, creando un bolo más grande.",
                "3. Verifique su proporción basal/bolo de carbohidratos, usualmente en adultos y adolecentes la proporcion basal es del (45-60%) de la TDD. Los bajas frecuentes suelen ser causadas por el que es más grande, mientras que las altas frecuentes son causado por el que es más pequeño.",
                "4. Si sus lecturas son relativamente estables y cercanas a su rango objetivo, un pequeño cambio en sus índices basales o bolos debería ser suficiente. Los carbohidratos (CarbF) generalmente cambian en un punto o menos, las correcciones (CorrF) de tres a diez puntos y tasas basales de 0,025 a 0,1 u/h pruebe cambios pequeños primero; pueden ser necesarios cambios más grandes para bajas o altas frecuentes.",
                "5. Cuando cambie la configuración de una bomba, determine cuánta más insulina que este cambio le dará cada día (Su bomba ¡realmente debería estar haciendo esto por ti!).",
                "6. Ajuste los factores de carbohidratos (CarbF) y corrección (CorrF) cada 4 días y las tasas basales cada 7 días minimo hasta que su control mejore.",
                "7. Para detener las lecturas bajas o altas, cambie los índices basales al menos de 5 a 8 horas antes de que ocurra la lectura y el CarbF (o CorrF) antes de la comida previa.",
                "8. Para resolver un patrón no deseado, verifique dos veces su actual dosis diaria total (TDD) de insulina contra los ajustes óptimos recomendados por la App según su TDD promedio actual y sus ajustes actuales de la bomba o en también en www.opensourcediabetes.org."));
*/
        resultadoTextArea.setText(resultadoApp.toString()); //Escribe resultado de los calculos contenido en el StringBuilder resultadoApp en el TextArea de la app (resultadoTextArea)  
        }
        catch(NumberFormatException ex){
            stateCalculatorLabel.setText("¡Todos los campos con (*) son requeridos!");
        }
    }
    
    @FXML
    private void reiniciarCalculator(ActionEvent event){
        nombreTextField.setText("");
        pesoTextField.setText("");
        ddtTextField.setText("");
        gsPromedioTextField.setText("");
        gsObjectivoTextField.setText("");
        patronSeleccionado = PatronGlucemico.NORMAL;
        //Reincia ToggleButton Hipoglicemia para que pide de nuevo una seleccion especifica entre BAJO Y MUY_BAJO, en caso que se seleccione en otro ejecución el patro HIPOGLICEMIAS FRECUENTES
        //hipoTButton.setUserData(patronSeleccionado); //Al reiniciar el PatronGlucemico vuelve a su valor por defecto NORMAL
        hipoTipoCombox.setItems(tiposHiposOList);
        hipoTipoCombox.setValue(null);
        hipoTipoCombox.setPromptText("Selecccione tipo de hipoglicemias más frecuentes"); //Muestro de nuevo el Promp Text en el ComboBox
        //reinicio los toggleButtons
        hipoTButton.setSelected(false);
        hipoTButton.setText("No");
        hipoEHiperTButton.setSelected(false);
        hipoEHiperTButton.setText("No");
        hiperTButton.setSelected(false);
        hiperTButton.setText("No");
        resultadoTextArea.setText("");
        guardarButton.setDisable(true);
        stateCalculatorLabel.setText("");
        a1cTextField.setText("");
    }
    
    @FXML
    private void exportarResultadoListener(ActionEvent event){
        stage = (Stage) vBox.getScene().getWindow(); //representa la ventana de la aplicación
        Date date = Date.from(Instant.now());
        String nombrePaciente = nombreTextField.getText(); 
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Resultado");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"), "."+File.separator+"Documents"+File.separator)); //Establece el lugar por defecto donde se gurdaran el archivo (File.separator en un cosntante para evita problemas de ejecución en otros sistemas operativos)        
        fileChooser.setInitialFileName(String.format("[%tb %te de %tY] Resultado %s.txt", date, date, date, nombrePaciente));
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text File [txt]", "*.txt"));
        File saveFile = fileChooser.showSaveDialog(stage);
        if(saveFile != null){
            try {
            Path resultSavedPath = Paths.get(saveFile.getAbsolutePath());
            Formatter result = new Formatter(saveFile);
            result.format("Paciente: %s%nFecha: %tA %tB %td %tY [%tl:%tM:%tS %tp]%n%n%s", (nombrePaciente != "" ? nombrePaciente : "Sin especificar"), date, date, date, date, date, date, date, date, resultadoApp.toString());//String.format("%s", resultadoTextArea.getText()));
            
            stateCalculatorLabel.setText(String.format("%s%n%s", "« ¡Resultado Exportado Exitosamente!, ver en »", saveFile.getCanonicalPath()));
            stateCalculatorLabel.setTextFill(Color.GREEN); //pinta texto de color verde
            stateCalculatorLabel.requestFocus(); //Pone focus sobre la etiqueta state
            
            //manejador evento anonimo que abre la ventana del explorador de archivos al hacer clic sobre la la etiqueta state...
            stateCalculatorLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    try{
                        Runtime.getRuntime().exec("explorer.exe /select," + saveFile.getPath().trim()); //Abre explorador de archivos en la ruta especificada
                    }
                    catch(IOException ex){
                        System.err.println("Exception: al intenter acceder a la ruta donde se guardo el resultado");
                    }
                }
            });
            
            //manejador evento anonimo para crear efecto al posicionar cursor del raton sobre la etiqueta state... 
            stateCalculatorLabel.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                   stateCalculatorLabel.setStyle("-fx-underline:true; -fx-text-fill:#50b8ea;"); //subraya y pinta de color el texto
                }
            });
            
            //manejador evento anonimo para crear efecto al quitar cursor del raton de la etiqueta state... 
            stateCalculatorLabel.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                    stateCalculatorLabel.setStyle("-fx-underline:false; -fx-text-fill:#49b637;  "); //desubraya y colorea texto
                }
            });
                if(result != null)
                    result.close(); //cierra el recurso Formatter
            } catch (IOException ex) {
                System.err.println("Error en la creación y manipulación del arhivo creado "+ex.getLocalizedMessage());
            }
        }
    }
    
    //Listener class as a Inner Class that hold a reference to TextField para de este modo conocer el objecto TextField que genera el evento.
    private class TextFieldListener implements ChangeListener<String> {
        private final TextField txtField; //variable de instancia que almacena campo TexteField que genera el evento
        
        public TextFieldListener(TextField txtField) {
            this.txtField = txtField;
        }
        
        @Override
        public void changed(ObservableValue<? extends String> ov, String oldV, String newV) {
            //Pattern regex = Pattern.compile("\\d{1,3}(?!\\d{1,3})\\.{1}\\d{1,2}|\\d{1,3}");
            //Matcher matcher = regex.matcher(newV);
            //while(!matcher.find()){
            stateCalculatorLabel.setText(""); //borra  cualquier advertencia que aparezca en la StateCalculatorLabel
            txtField.pseudoClassStateChanged(errorClass, false); //Cambia de nuevo a False el estado del Stylo errorClass
            
            if(!newV.matches("\\d{1,3}\\.?\\d*")){
            //"\\d\\.?\\d*$")){ //se verifica que el campo solo cotenga numeros ej: 32, 69, 78.5, ... de lo contario muestra el error en la etiqueta State
                txtField.pseudoClassStateChanged(errorClass, true);
                Platform.runLater(()->{//se usa para evitar la exception IlegalArgumentException al intentar modificar propiedad (en este caso el TextField) dentro de un Listener
                    //txtField.setText("");
                    txtField.clear();
                    stateCalculatorLabel.setText("Entrada ( " + newV + " ) No válida, solo numeros Ej: 7, 6.8, 9.56, 78, 67.8, 89.99, 107, 179.3 ó 187.97 ");
                });   
            }
            else if(txtField.isFocused()){ //Si escribio un numero en el campo y este es el que tiene el foco borra los mensajes de advertencia del stateCalculatorLabel    
                stateCalculatorLabel.setText("");
                txtField.pseudoClassStateChanged(errorClass, false); //Cambia de nuevo a False el estado del Stylo errorClass                   

            }
        }
    }
    
    private class GlucoseTextFieldListener implements ChangeListener<String> {
        private final TextField textField;

        public GlucoseTextFieldListener(TextField textField) {
            this.textField = textField;
        }
        
        @Override
        public void changed(ObservableValue<? extends String> ov, String oldValue, String newValue) {
            /*if(!newValue.matches("\\d{2,3}")){
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        textField.clear();
                        stateCalculatorLabel.setText("Entrada ( " + newValue + " ) No válida, solo numeros Ej: 8, 78 ó 187");

                    }
                });*/
            stateCalculatorLabel.setText(""); //borra cualquier advertencia que aparezca en la StateCalculatorLabel
            textField.pseudoClassStateChanged(errorClass, false); //Cambia al estilo por defecto del TextField
            try{
                double value = Double.valueOf(newValue);
                if(textField.equals(gsObjectivoTextField)){ //si el campo de texto es el de GS Objetivo
                    if(value < 100 || value > 180){
                       stateCalculatorLabel.setText("Revisar Glucosa Objetivo ( " + newValue + " ) !!!, prefiera valor de glucosa objetivo en el rango 100-180 mg/dl");
                       textField.pseudoClassStateChanged(errorClass, true); //Pone el Style .error para el textField (Este estilo se agrego antes en SceneBulder en la seccion clases de Style para el nodo TextField)  
                       gsObjectivoTextField.setUserData(stateCalculatorLabel.getText()); //guarda el tipo de error señalado en UserData del campo, con el fin poderlo recuperar nuevamente en los EventHandle del campo correspondiente
                    }
                    else if(textField.isFocused()){
                        stateCalculatorLabel.setText("");
                        textField.pseudoClassStateChanged(errorClass, false); //Cambia a False el estado de la clase en el Style.css
                        gsObjectivoTextField.setUserData(null);//si no existe error en el campo, borra UserData del campo
                    }
                }
                else{ //Para el campo de texto GS Promedio
                    if(value < 80 || value > 380){
                       stateCalculatorLabel.setText("Revisar Glucosa promedio ( " + newValue + " ) !!!, prefiera valor de glucosa promedio en el rango 80-380 mg/dl");
                       textField.pseudoClassStateChanged(errorClass, true);
                       gsPromedioTextField.setUserData(stateCalculatorLabel.getText()); //guarda el tipo de error señalado en UserData del campo, con el fin poderlo recuperar nuevamente en los EventHandle del campo correspondiente
                    }
                    else if(textField.isFocused()){
                        stateCalculatorLabel.setText("");
                        textField.pseudoClassStateChanged(errorClass, false); //Cambia a False el estado de la clase en el Style.css
                        gsPromedioTextField.setUserData(null); //si no existe error en el campo, borra UserData del campo
                    }
                }
            }
            catch(NumberFormatException ex){
                System.err.println("Exception conversion a double: "+ex.getMessage());
            }
        }  
    }
}