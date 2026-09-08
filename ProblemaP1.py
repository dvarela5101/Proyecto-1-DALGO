import sys


def leer_datos(entrada):
    pos = 0
    cantidad_casos = entrada[pos]
    pos = pos + 1

    casos = []
    for c in range(cantidad_casos):
        n = entrada[pos]
        m = entrada[pos + 1]
        p = entrada[pos + 2]
        pos = pos + 3

        energias = entrada[pos : pos + n]
        pos = pos + n

        portales = []
        for i in range(p):
            xs = entrada[pos]
            ys = entrada[pos + 1]
            xe = entrada[pos + 2]
            ye = entrada[pos + 3]
            portales.append((xs, ys, xe, ye))
            pos = pos + 4

        casos.append((n, m, p, energias, portales))

    return casos


def calcular_orbita(m, e, fila):
    # De izquierda a derecha: ¿me conviene venir caminando desde mi vecino izquierdo?
    for j in range(1, m):
        if fila[j - 1] + e < fila[j]:
            fila[j] = fila[j - 1] + e

    # De derecha a izquierda: ¿me conviene venir caminando desde mi vecino derecho?
    for j in range(m - 2, -1, -1):
        if fila[j + 1] + e < fila[j]:
            fila[j] = fila[j + 1] + e

    return fila


def consumo_minimo_energia(n, m, energias, portales):
    desconocido = 10**30

    # Matriz de costos: mejor_costo[i][j] = menor energía para llegar a la órbita i, posición j.
    mejor_costo = []
    for i in range(n):
        fila = []
        for j in range(m):
            fila.append(desconocido)
        mejor_costo.append(fila)

    mejor_costo[0][0] = 0

    # Agrupamos los portales por su órbita de origen, UNA sola vez,
    # para no tener que revisar la lista completa en cada órbita.
    portales_por_orbita = [[] for _ in range(n)]
    for (xs, ys, xe, ye) in portales:
        portales_por_orbita[xs - 1].append((ys, xe, ye))

    # Procesamos las órbitas en orden: 0, 1, 2, ..., n-1
    for i in range(n):
        # Paso 1: propagar la energía dentro de esta órbita
        mejor_costo[i] = calcular_orbita(m, energias[i], mejor_costo[i])

        # Paso 2: usar solo los portales que salen de esta órbita
        for (ys, xe, ye) in portales_por_orbita[i]:
            costo = mejor_costo[i][ys - 1]
            if costo < mejor_costo[xe - 1][ye - 1]:
                mejor_costo[xe - 1][ye - 1] = costo

    respuesta = mejor_costo[n - 1][m - 1]

    if respuesta >= desconocido:
        return None
    return respuesta


entrada = list(map(int, sys.stdin.read().split()))
casos = leer_datos(entrada)

for n, m, p, energias, portales in casos:
    resultado = consumo_minimo_energia(n, m, energias, portales)

    if resultado is None:
        print("NO EXISTE")
    else:
        print(resultado)