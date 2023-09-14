package otr.helper.bpmn.report

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.codehaus.groovy.runtime.ResourceGroovyMethods
import otr.helper.bpmn.request.param.ParamRequest


ParamRequest paramRequest = new ParamRequest();
def  flag = true;
def coStatus = paramRequest.getParam("coStatus",flag)
def statementStatus = paramRequest.getParam("statementStatus",flag)
def profileStatus = paramRequest.getParam("profileStatus",flag)
def contractStatus = paramRequest.getParam("contractStatus",flag)
def wdType = paramRequest.getParam("wdType",flag)
def insuranceStatus = paramRequest.getParam("insuranceStatus",flag)
def insurancePremiumStatus = paramRequest.getParam("insurancePremiumStatus",flag)
def actStatus = paramRequest.getParam("actStatus",flag)
def agreementStatus = paramRequest.getParam("agreementStatus",flag)
def functionMap = paramRequest.getParam("functionMap",flag)
def createItem = paramRequest.getParam("createItem",flag)
def notifyMap = paramRequest.getParam("notifyMap",flag)
def petitonNumber = paramRequest.getParam("petitonNumber",flag)
def config = paramRequest.getParam("config",flag)


import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
// статусы, по которым отправляем уведомления
def mapStatusSla = [:]
def map2 = [:]
map2.put(coStatus.new, "АО «ЭКСАР» напоминает, что Вам необходимо согласовать Коммерческое предложениеTERM")
mapStatusSla.put(wdType.co, map2)

map2 = [:]
map2.put(statementStatus.dash, "АО «ЭКСАР» напоминает, что Вам необходимо заполнить/исправить/ переподписать Заявление на страхованиеTERM")
map2.put(statementStatus.draft, "АО «ЭКСАР» напоминает, что Вам необходимо заполнить/исправить/ переподписать Заявление на страхованиеTERM")
map2.put(statementStatus.edit, "АО «ЭКСАР» напоминает, что Вам необходимо заполнить/исправить/ переподписать Заявление на страхованиеTERM")
mapStatusSla.put(wdType.statement, map2)

map2 = [:]
map2.put(profileStatus.dash, "АО «ЭКСАР» напоминает, что Вам необходимо подписать и отправить Анкету страхователяTERM")
map2.put(profileStatus.draft, "АО «ЭКСАР» напоминает, что Вам необходимо подписать и отправить Анкету страхователяTERM")
map2.put(profileStatus.edit, "АО «ЭКСАР» напоминает, что Вам необходимо подписать и отправить Анкету страхователяTERM")
mapStatusSla.put(wdType.profile, map2)

map2 = [:]
map2.put(contractStatus.new, "АО «ЭКСАР» напоминает, что ваша Заявка была согласована. Для получения услуги Вам необходимо согласовать Проект договораTERM CONTRACTDOPTEXT")
// добаляем - В случае несогласования Проекта договора в установленные сроки Заявка № [Номер заявки] будет переведена в статус Завершена (Отказ Клиента)
mapStatusSla.put(wdType.contract, map2)

map2 = [:]
map2.put(insuranceStatus.sign, "АО «ЭКСАР» напоминает, что Вам необходимо подписать Договор страхованияTERM")
mapStatusSla.put(wdType.insurance, map2)

map2 = [:]
map2.put(insurancePremiumStatus.dash, "Договор страхования № INSURANCEDOCNUMBER от INSURANCEDOCDATA на получение услуги был подписан. Вам необходимо оплатить страховую премию по реквизитам АО «ЭКСАР», указанным в договоре страхованияTERM")
map2.put(insurancePremiumStatus.pay,  "Договор страхования № INSURANCEDOCNUMBER от INSURANCEDOCDATA на получение услуги был подписан. Вам необходимо оплатить страховую премию по реквизитам АО «ЭКСАР», указанным в договоре страхованияTERM")
mapStatusSla.put(wdType.insurancePremium, map2)

map2 = [:]
map2.put(actStatus.sign, "Вам необходимо подписать Страховой актTERM")
mapStatusSla.put(wdType.act, map2)

map2 = [:]
map2.put(agreementStatus.new, "АО «ЭКСАР» напоминает, что Вам необходимо согласовать Дополнительное соглашениеTERM")
map2.put(agreementStatus.sign, "АО «ЭКСАР» напоминает, что Вам необходимо подписать Дополнительное соглашениеTERM")
mapStatusSla.put(wdType.agreement, map2)

// для писем
map2 = [:]
map2.put("Получен", "Вам необходимо отправить ответ на входящее письмо с обращением № NUMBERMAILTERM")
mapStatusSla.put("mails", map2)

// заполним переменный по умолчанию
def mapReplace = [:]
mapReplace.put("INSURANCEDOCNUMBER", createItem.workDocuments.find{ it.workDocType == wdType.insurance && it.status == insuranceStatus.signed}?.number ?: "")
mapReplace.put("INSURANCEDOCDATA", createItem.workDocuments.find{ it.workDocType == wdType.insurance && it.status == insuranceStatus.signed}?.signDate ?: "")
mapReplace.put("NUMBERMAIL", "")
mapReplace.put("TERM", "")
mapReplace.put("CONTRACTDOPTEXT", "")
def CONTRACTDOPTEXT = "В случае несогласования Проекта договора в установленные сроки Заявка № " + petitonNumber + " будет переведена в статус Завершена (Отказ Клиента)";

// получатели уведомлений, в зависимости от статуса.

def mapSlaHuman = [:]
map2 = [:]
map2.put(coStatus.new, ["createItemSend","createItemContact"])
mapSlaHuman.put(wdType.co, map2)

map2 = [:]
map2.put(statementStatus.dash, ["createItemSend","createItemContact"])
map2.put(statementStatus.draft, ["createItemSend","createItemContact"])
map2.put(statementStatus.edit, ["signer"])
mapSlaHuman.put(wdType.statement, map2)

map2 = [:]
map2.put(profileStatus.dash, ["createItemSend", "createItemContact"])
map2.put(profileStatus.draft, ["createItemSend", "createItemContact"])
map2.put(profileStatus.edit, ["createItemSend", "createItemContact", "sender", "drafter"])
mapSlaHuman.put(wdType.profile, map2)

map2 = [:]
map2.put(contractStatus.new, ["createItemSend","createItemContact","insurance_statementSend"])
mapSlaHuman.put(wdType.contract, map2)

map2 = [:]
map2.put(insuranceStatus.sign, ["createItemSend","createItemContact","insurance_statementSend","sender"])
mapSlaHuman.put(wdType.insurance, map2)

map2 = [:]
map2.put(insurancePremiumStatus.dash,["createItemSend","createItemContact","sender","insurance_statementSend"] )
map2.put(insurancePremiumStatus.pay, ["createItemSend","createItemContact","sender","insurance_statementSend"])
mapSlaHuman.put(wdType.insurancePremium, map2)

map2 = [:]
map2.put(actStatus.sign, ["eio","createItemContact","insuranceSend"])
mapSlaHuman.put(wdType.act, map2)

map2 = [:]
map2.put(agreementStatus.new, ["createItemContact","eio", "insuranceSend"])
map2.put(agreementStatus.sign, ["createItemContact","eio","insuranceSend"])
mapSlaHuman.put(wdType.agreement, map2)

// для писем
map2 = [:]
map2.put("Получен", ["eio", "createItemContact","insuranceSend"])
mapSlaHuman.put("mails", map2)

private static LocalDate getWorkDay(LocalDate todayWorkDay, int day, Map calendarMap) {
    def nextDateTime = todayWorkDay//LocalDate.parse(todayWorkDay, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    for (int i = 1; i <= day; i++) {
        nextDateTime = nextDateTime.plusDays(1)
        def key = nextDateTime.toString()
        def isWorkDay = true
        if (calendarMap.get(key) == null) {
            isWorkDay = true
        } else {
            isWorkDay = calendarMap.get(key) == "workday"
        }
        while (!isWorkDay) {
            nextDateTime = nextDateTime.plusDays(1)
            key = nextDateTime.toString()
            if (calendarMap.containsKey(key)) {
                isWorkDay = calendarMap.get(key) == "workday"
            } else {
                isWorkDay = true
            }
        }
    }
    return nextDateTime;
}

private static String replaceMail(Map map, String str){
    map.each {str = str.replace(it.key, it.value)}
    return str;
}

def listMailResult = []
def patternDB = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
def pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
def patternMailDeadline = DateTimeFormatter.ofPattern("dd.MM.yyyy");
def day = config.timer.daysDefault;
def dayDeadline = config.timer.daysDeadline;

// проверим рабочие документы
createItem.workDocuments
        .findAll { it.deadline == null || (it.deadline != null && (LocalDate.now().isBefore(LocalDate.parse(it.deadline, pattern)) || LocalDate.now().isEqual(LocalDate.parse(it.deadline, pattern)))) }
        .each {
            if (mapStatusSla.get(it.workDocType) != null && mapStatusSla.get(it.workDocType).get(it.status) != null) {
                // проверим, нужно или нет отправлять уведомление сегодня
                def deadline = (it.deadline != null);
                // дата получения документа
                def sendDate = LocalDateTime.parse(it.dateFrom, patternDB).toLocalDate();
                // получим дату напоминания
                sendDate = getWorkDay( sendDate, (deadline ? dayDeadline : day) , calendarMap)
                while(LocalDate.now().isAfter(sendDate)){
                    sendDate = getWorkDay( sendDate, (deadline ? dayDeadline : day) , calendarMap)
                }
                if(LocalDate.now().isEqual(sendDate)) {
                    // сформируем получателей
                    def key = wdType.find { item -> item.value == it.workDocType }?.key

                    def uuid = it.uuid;

                    if(key == "statement") {
                        key = "insurance_statement"
                    }

                    // получим отправителей из договора страхования
                    if(key == "insurancePremium") {
                        key = "insurance"
                        uuid = createItem.workDocuments.find{ it.workDocType == wdType.insurance && it.status == insuranceStatus.signed}?.uuid
                    }

                    def listHuman = mapSlaHuman.get(it.workDocType)?.get(it.status)
                    // уберем дубликаты получателей
                    def mapEMail = [:]
                    listHuman.each { str ->
                        def email = notifyMap.get(key)?.get(uuid)?.last()?.get(str)?.email
                        if(email instanceof List)
                            email = email.join(";")
                        if (email != null && email != "") {
                            mapReplace.replace("TERM", deadline ? (" в срок не позднее " + LocalDate.parse(it.deadline, pattern).format(patternMailDeadline) + ".") : ".")
                            mapReplace.replace("CONTRACTDOPTEXT", deadline ? CONTRACTDOPTEXT : "")
                            mapEMail.put(email, replaceMail(mapReplace, mapStatusSla.get(it.workDocType)?.get(it.status)) )
                        }
                    }
                    mapEMail.each {listMailResult.add([ "to" : it.key, "body" : it.value])}
                }
            }
        }

// проверим почту
createItem.mails.findAll{ it.mailType == "Входящее" &&
        it.status == "Получен" &&
        (it.answerDeadline == null || it.answerDeadline == "-" ||  (it.answerDeadline  != null && (LocalDate.now().isBefore(LocalDate.parse(it.answerDeadline , patternMailDeadline)) || LocalDate.now().isEqual(LocalDate.parse(it.answerDeadline , patternMailDeadline))))) &&
        createItem.mails.find{ item ->
            item.mailType == "Исходящее" &&
                    item.prevMailId != null &&
                    item.prevMailId == it.uuid &&
                    item.status != mailStatus.draft &&
                    item.status != mailStatus.onSigning} == null
}
        .each {
            def deadline = (it.answerDeadline != null && it.answerDeadline != "-");
            // дата получения документа
            def sendDate = LocalDateTime.parse(it.dateFrom, patternDB).toLocalDate();
            // получим дату напоминания
            sendDate = getWorkDay( sendDate, (deadline ? dayDeadline : day) , calendarMap)
            while(LocalDate.now().isAfter(sendDate)){
                sendDate = getWorkDay( sendDate, (deadline ? dayDeadline : day) , calendarMap)
            }

            if(LocalDate.now().isEqual(sendDate)) {
                // сформируем получателей
                def listHuman = mapSlaHuman.get("mails")?.get(it.status)
                // уберем дубликаты получателей
                def mapEMail = [:]
                listHuman.each { str ->
                    def email = notifyMap.get("mail")?.get(it.uuid)?.last()?.get(str)?.email
                    if(email instanceof List)
                        email = email.join(";")

                    if (email != null && email != "") {
                        mapReplace.replace("TERM", deadline ? (" в срок не позднее " + it.answerDeadline + ".") : ".")
                        mapReplace.replace("NUMBERMAIL", it.appealNumber)
                        mapEMail.put(email, replaceMail(mapReplace, mapStatusSla.get("mails")?.get(it.status)))
                    }
                }
                mapEMail.each {listMailResult.add([ "to" : it.key, "body" : it.value])}
            }

        }

println(listMailResult)