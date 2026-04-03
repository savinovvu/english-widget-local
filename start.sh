#!/bin/bash
# Загружаем asdf
source /home/skorpion/.asdf/asdf.sh
# Загружаем профиль пользователя
source /home/skorpion/.profile

# Переходим в рабочую директорию
cd /home/skorpion/1.Soft/1.projects/2.myprojects/eng/eng

# Запускаем приложение
exec /home/skorpion/.asdf/shims/java -Djava.awt.headless=false -jar exec/eng-application.jar