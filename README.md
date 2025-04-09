# ElevatorMultiThreading

Program wielowątkowy symulujący działanie windy w pięcio-poziomowym budynku.
Animacja wykonana za pomocą JavaFX

Istnieje jeden wątek dla windy, i tworzone są w trakcie działania kolejne wątki symulujące ludzi/pasażerów.
Wątki są odpalane przez Thread.start lub ExecutorService.

Winda obsługuje pasażerów w kolejności jej zamówienia (ale 'zbiera' też pasażerów jadących w tą samą stronę w którą winda i tak jedzie).
Algorytm windy nie jest nadmiernie skomplikowany ponieważ skupiono się na obsłudze wielu wątków.

## Profiler - Thread vs ExecutorService

### Thread:

![thread1](img/threadsProfile1.png)
![thread2](img/threadsProfile2.png)
![thread3](img/threadsProfile3.png)

### ExecutorService:

![executor1](img/executorProfile1.png)
![executor2](img/executorProfile2.png)
![executor3](img/executorProfile3.png)

