# PumpSettingsCalculator
Pump Settings Calculator es una aplicación desarrollada 100% en lenguaje Java cuyo principal objetivo es proveer una manera rápida y segura para calcular los valores óptimos requeridos para iniciar u optimizar un tratamiento con bomba de insulina.

# Pump Settings Calculator App

![Diabetes Calculator App](https://i.ibb.co/25dYLv3/Captura-Diabetes-Calculator-App-1.png%22%20alt=%22Captura-Diabetes-Calculator-App-1)
Pump Settings Calculator es una aplicación desarrollada 100% en lenguaje Java cuyo principal objetivo es proveer una manera rápida y segura para calcular los valores óptimos requeridos para iniciar u optimizar un tratamiento con bomba de insulina. La aplicación solicita seis datos de los cuales cuatro son obligatorios como son peso, dosis diaria total de insulina (TDD), glucosa promedio y glucosa objetivo para arrojar el resultado y los otros dos opcionales como lo son el nombre y/o identificación y el patrón glucémico: 

 1. Nombre y/o Identificación (Opcional): permite personalizar el resultado con el nombre y/o identificación del paciente.
 2. *Peso [Kg] (Requerido):* el peso actual del usuario en kilogramos.
 3. *TDD o Dosis diaria total de insulina [u/día] *(Requerido)*:* promedio de la dosis diaria total de insulina que se requiere al día durante los últimos 7 a 14 días idealmente, incluye insulina basal e insulina rápida dada en bolos de comida y de corrección.
 4. *Glucosa en sangre promedio [mg/dl] *(Requerido)*:* glucosa en sangre promedio durante 14 días o más, para facilitar la obtención del dato la aplicación incluye una calculadora* que permite calcular el valor promedio de glucosa a partir de un valor reciente de HbA1c (Hemoglobina glicosilada).
 5. *Glucosa en sangre objetivo [mg/dl] *(Requerido)*:* glucosa en sangre objetivo en el que se desea estar la mayor parte del tiempo.
 6. *Patrón Glucémico* (Opcional): Señalar solo una de las tres opciones de patrón disponibles, si se presentan episodios constantes de hipoglicemias (Bajo entre 65 - 54 mg/dl) o (Muy Bajo inf. a 50 mg/dl), Hiperglicemias frecuentes en distintos momentos del día o tanto hipoglicemias e hiperglicemias de forma recurrente.

*Calculadora Promedio Glucosa a partir de HbA1c (Hemoglobina Glicosilada): la aplicación simplifica el proceso para obtener la glucosa promedio con la inclusión de una calculadora que usa la fórmula ADAG (_A1c-Derived Average Glucose_) utilizada para estimar la glucosa promedio (eAG, por sus siglas en inglés) a partir de la A1c de la siguiente manera:

> Blockquote

28,7 * A1c (%) - 46,7 = eAG (miligramos/decilitro; mg/dL)

Si se toma como ejemplo un valor de A1c de 6 el cálculo resultaría:

28,7 * 6 - 46,7 = 126 mg/dL, dando lugar a una glucosa promedio de 126 mg/dL.

Esto significa que para cada unidad de incremento del porcentaje de hemoglobina glicada (HbA1c), la glucosa promedio aumenta aproximadamente 29 mg/dL.

> Blockquote

El calculo que realiza la aplicación permite obtener los valores promedios de la tasa de insulina **basal**, el **ratio** (Insulina a Carbohidrato) correspondiente a los gramos de carbohidratos cubiertos por una unidad de insulina y la **corrección** correspondiente a la caída de glucosa ocasionada por una unidad de insulina para el ***TDD actual***, ***TDD esperado*** y ***TDD mejorado*** (iTDD); el resultado obtenido es personalizado en base a los datos proporcionados y puede ser exportado en formato **txt** para ser consultado posteriormente cuando así lo desee.
![Diabetes Calculator App](https://i.ibb.co/1KTtSYD/Captura-Diabetes-Calculator-App-2.png)

**Fuentes utilizadas:**
La aplicación en su totalidad tiene su base en las datos  y formulas contenidas en el libro Pumping insulin de John Walsh (PA) y Ruth Roberts (2017).

**Codificado por:**
Ermitaño Bohemio

Imagen App 1: [https://imgbb.com/KmWzQ2x](https://imgbb.com/KmWzQ2x)

Imagen App 2: [https://imgbb.com/2Pd1b0D](https://imgbb.com/2Pd1b0D)
> Written with [StackEdit](https://stackedit.io/).
