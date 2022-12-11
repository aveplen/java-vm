## Структура проекта

`src/main/java/` <- тут весь код

`             /.../app`      <- точки входа в приложения

`             /.../compiler` <- компилятор (+лексер) (практическая 3)

`             /.../output`   <- форматирование вывода дампа виртуальной машины

`             /.../tasks`    <- описание задачи свёртки для gpu (практическая 2)

`             /.../vm`       <- виртуальная машина (практическая 1)

`src/test/java` <- тесты

## Сборка и запуск

Для сборки используется [Apache Maven](https://maven.apache.org/download.cgi), сборка настроена так,
что на стадии `package` создаются три исполняемых jar-а: `vm.jar`, `compiler.jar` и `gpu.jar`

Параметры запуска `vm.jar`:
1. -i --input - путь к файлу с инструкциями - обязательный
2. -v --verbose - напечатать дамп виртуальной машины после окончания исполнения
3. Остальные переданные аргументы переносятся в память программы в неизменном виде

Параметры запуска `compiler.jar`:
1. -i --input - путь к файлу с исходным кодом - обязательный
2. -o --output - путь к файлу с инструкциями (может не существовать) - обязательный
3. -v --verbose - напечатать дамп виртуальной машины после окончания исполнения

У `gpu.jar` параметров запуска нет 

### Пример сборки и запуска

```shell
mvn clean package
cd target

# компиляция
java -jar compiler.jar -v  -i ../arr_sum.raw -o ../arr_sum.compiled
#...list of tokens

java -jar vm.jar -v -i ../arr_sum.compiled 4 10 11 12 13
46
#...dump

java -jar gpu.jar
# matrix 1

# matrix 2

# multiplication result

```

## Архитектура

Вариант 0000:
1. Размер команды 64 бита (потому что Java, так можно и 16)
2. Гарвардская архитектура (инструкции и данные в разных устройствах)
3. Задача на написание кода: сумма элементов в массиве

| Код  | Инструкция | Описание                                                                                       |
|------|------------|------------------------------------------------------------------------------------------------|
| 0x00 | NOOP       | Ничего                                                                                         |
| 0x01 | ADD        | Сумма двух верхних элементов на стеке                                                          |  
| 0x02 | SUB        | Разность двух вехних элементов на стеке `a = pop(), b = pop(), push(b - a)`                    |
| 0x03 | AND        | Побитовое "И" двух вехних элементов на стеке                                                   |
| 0x04 | OR         | Побитовое "ИЛИ" двух вехних элементов на стеке                                                 |
| 0x05 | XOR        | Побитовое "ИСКЛ. ИЛИ" двух вехних элементов на стеке                                           |
| 0x06 | NOT        | Побитовое "НЕ" вехнего элемента на стеке                                                       |
| 0x07 | IN         | Пользовательский ввод одного машинного слова на вершину стека                                  |
| 0x08 | OUT        | Вывод одного машинного слова с вершины стека                                                   |
| 0x09 | LOAD       | Загрузка значения из памяти на вершину стека `addr = pop(), push(memory[addr])`                |
| 0x0A | STOR       | Выгрузка значения с вершины стека в память `addr = pop(), value = pop(), memory[addr] = value` |
| 0x0B | JMP        | Безусловный переход на адрес, указанный на вершине стека                                       |
| 0x0C | JZ         | Переход, если на вершине стека ноль `a = pop(), addr = pop(), if (a == 0) goto addr`           |
| 0x0D | JNZ        | Переход, если на вершине стека не ноль `a = pop(), addr = pop(), if (a != 0) goto addr`        |
| 0x0E | PUSH       | Добавление следующего машинного слова на стек, значение берется из файла с инструкциями        |
| 0x0F | DUP        | Дублирование вершины стека                                                                     |
| 0x10 | SWAP       | Перемена мест двух верхних элементов стека                                                     |
| 0x11 | ROLR       | Вращение трёх верхних элементов вправо `a b c >  => c a b >`                                   |
| 0x12 | ROLL       | Вращение трёх верхних элементов влево `a b c >  => b c a >`                                    |
| 0x13 | DROP       | Удаление элемента с вершины стека                                                              |
| 0x14 | COMP       | Отрицание вершины стека `a = pop(), push(-a)`                                                  |
| 0x15 | CDEC       | Декремент рагистра-счетчика на 1                                                               |
| 0x16 | CINC       | Инкремент рагистра-счетчика на 1                                                               |
| 0x17 | CTS        | Перенос значения из регистра-счетчика на вершину стека                                         |
| 0x18 | STC        | Перенос значения с вершины стека в регистр-счетчик                                             |
| 0x19 | TERM       | Завершение работы программы                                                                    |
| 0x1A | OUTNH      | Вывод машинного слова на вершине стека без его снятия                                          |
| 0x1B | MUL        | Произведение двух верхних элементов на стеке                                                   |

## Исходники для виртуальной машины

### Поиск суммы элементов массива

(первым элементом массива обязательно должна быть его длина)

```
/* 01 */   push                 // load array length from memory 0x00
/* 02 */   0                    //
/* 03 */   load                 //
/* 04 */   dup                  //
                                //
/* 05 */   push                 // if len(arr) == 0 then goto end, nothing to do
/* 06 */   &final_routine       //
/* 07 */   swap                 //
/* 08 */   jz                   //
                                //
/* 09 */   stc                  // move array length from stack top to counter reg
                                //
                                //
/* 10 */   while_1:             //
/* 11 */   cts                  //
/* 12 */   load                 //
/* 13 */   cdec                 //
/* 14 */   cts                  //
/* 15 */   push                 //
/* 16 */   &sum_routine         // when counter == 0, goto sum_routine
/* 17 */   swap                 //
/* 18 */   jz                   //
/* 19 */   push                 //
/* 20 */   &while_1             //
/* 21 */   jmp                  //
                                //
                                //
/* 22 */   sum_routine:         // sum values stored on a stack
/* 23 */   push                 //
/* 24 */   0                    //
/* 25 */   load                 //
/* 26 */   stc                  //
                                //
/* 27 */   cdec                 // amount of operations = len(arr)-1
                                //
                                //
/* 28 */   while_2:             // while counter != 0
/* 29 */   cts                  //
/* 30 */   push                 //
/* 31 */   &final_routine       // when counter == 0, goto final_routine
/* 32 */   swap                 //
/* 33 */   jz                   //
                                //
/* 34 */   add                  //
/* 35 */   cdec                 //
/* 36 */   push                 //
/* 37 */   &while_2             //
/* 38 */   jmp                  //
                                //
                                //
/* 39 */   final_routine:       // store result in memory 0x00
/* 40 */   outnh                //
/* 41 */   term                 //
```

### Свёртка двух массивов

(длины массивов должны быть одинаковыми + длина первым элементом)

```
/* 00 */   start:             //
/* 01 */     push             // load arr len
/* 02 */     0                //
/* 03 */     load             //
                              //
/* 04 */     dup              // save first length into arr_len1
/* 05 */     push             //
/* 06 */     0                // arr len
/* 07 */     stor             //
                              //
/* 08 */     dup              // goto final_routine if arr len == 0
/* 09 */     push             //
/* 10 */     &final_routine   //
/* 11 */     swap             //
/* 12 */     jz               //
                              //
/* 13 */     stc              // counter = arr len
                              //
/* 14 */   mult_routine:      //
/* 15 */     cts              // get counter
/* 16 */     load             // load element of the first array (counter)
                              //
/* 17 */     cts              // get counter
/* 18 */     push             //
/* 19 */     0                // push arr len addr
/* 20 */     load             // load arr len
/* 21 */     push             //
/* 22 */     1                //
/* 23 */     add              //
/* 24 */     add              //
/* 25 */     load             // load element of the second array (counter + len1 + 1)
                              // additional 1 is for arr2 len, which is not used in program
                              //
/* 26 */     mul              // arr1[i] * arr2[i]
                              //
/* 27 */     cdec             // counter --
                              //
/* 28 */     cts              // jump to sum_routine if counter == 0
/* 29 */     push             //
/* 30 */     &sum_routine     //
/* 31 */     swap             //
/* 32 */     jz               //
                              //
/* 33 */     push             // if counter != 0 continue multiplying on a stack
/* 34 */     &mult_routine    //
/* 35 */     jmp              //
                              //
/* 36 */   sum_routine:       //
/* 37 */     push             // counter = len(arr)-1
/* 38 */     0                // arr len
/* 39 */     load             //
/* 40 */     stc              //
/* 41 */     cdec             //
                              //
/* 42 */   while:             //
/* 43 */     add              // arr1[i-1]*arr2[i-1] + arr1[i]*arr2[i]
/* 44 */     cdec             // counter --
                              //
/* 45 */     cts              // if counter == 0 goto final_routine
/* 46 */     push             //
/* 47 */     &final_routine   //
/* 48 */     swap             //
/* 49 */     jz               //
                              //
/* 50 */     push             // goto while
/* 51 */     &while           //
/* 52 */     jmp              //
                              //
/* 53 */   final_routine:     //
/* 54 */     outnh            //
/* 55 */     term             //
```
