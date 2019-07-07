## Nonotibot
El código detrás de @Nonotibot (Twitter)

# Descripción
Programa en Java que genera frases a partir de reglas. El programa incluirá en el futuro una integración con la API Twitter4j para poder tuitear las frases generadas.

# Cómo funciona

Para poder generar frases, se necesitan crear reglas en ficheros de texto plano (x). Las reglas tienen el siguiente formato:
 
 $REGLA -> {"texto", @p1=A1, @p2=A2, ..., @pN=AN}
 
 Donde $REGLA es el identificador o la parte izquierda de la regla, separada por la flecha ->. Todo lo que va entre comillas es el texto de la regla, que puede incluir más identificadores. Todos los identificadores deben comenzar obligatoriamente por el dólar ($). En lugar de texto, se puede poner \_, como representación de lambda o cadena vacía, por ejemplo:
 
 $LAMBDA -> {\_}
 
 Las propiedades de la regla son tres: género, número y _set_.
 1) @g para elegir género, puede ser M (masculino), F (femenino) o ? (al azar).
 2) @n para elegir número, puede ser S (singular), P (plural) o ? (al azar).
 3) @s para determinar si esta regla puede elegirse y cambiar a la vez el género o número a uno concreto. Puede ser T (true) o F (false).
 Estas propiedades sirven para que una regla sea capaz de generar un contexto. Por ejemplo, podemos poner @g=? y @n=? para que una regla de un sintagma nominal decida aleatoriamente el género y el número.
 
 El texto de la regla también tiene formato propio:
 
 1) Texto plano, simples caracteres alfanuméricos y signos de puntuación.
 2) Identificadores, que empiezan con el dólar ($)
 3) Elección por género. Para decidir entre <masculino|femenino>
 4) Elección por número. Para decidir entre (singular|plural) o sólo (plural)
 5) Listas. Se escogerá un valor aleatorio de entre todos los posibles [a|b|c|...]
 
Ejemplos:

$IRRUPTOR_OPERACION -> {"$SUJETO [entra(n)|irrumpe(n)] $COMO_IRRUMPE en el quirófano"}
$COMO_IRRUMPE -> {"[alocadamente|impresiviblemente|forzosamente|$LAMBDA]"}
