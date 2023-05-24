package otr.helper.bpmn.report

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.codehaus.groovy.runtime.ResourceGroovyMethods
import otr.helper.bpmn.request.param.ParamRequest


ParamRequest paramRequest = new ParamRequest();
def insurance_statement = paramRequest.getParam("insurance_statement",true)
def functionMap = paramRequest.getParam("functionMap",true)
def clientOrg = paramRequest.getParam("clientOrg",true)
println(paramRequest.getParam("petitonNumber",true))



def checkTrue = "<span>&#9746;</span>"
def checkFalse = "<span>&#9744;</span>"

def clientAddress(clientOrg){
    def address = clientOrg.extAddresses.find({item -> {
        item.extAddressType == "legal"
    }
    })
    if(address != null){
        if(address.extAddress != null){
            return address.extAddress
        }
        else {
            def result = ""
            if(address.extRegion != null){
                result+=address.extRegion + " "
            }
            if(address.extStreet != null){
                result+=address.extStreet + " "
            }
            if(address.extHouse != null){
                result+=address.extHouse + " "
            }
            if(address.extBuilding != null){
                result+=address.extBuilding + " "
            }
            return result;
        }
    }
    return null;
}

def contractInsurance(contact){
    return contact.surname + " " + contact.name + " " + contact.patronymic
}

def prev_payment_obligations_info_other(insurance_statement) {
    if (insurance_statement.jsonData.export_contract.price_detals.any_prev_payment_obligations == "yes") {
        def prev_payment_obligations_info_other_str = insurance_statement.jsonData.export_contract.price_detals.prev_payment_obligations_info.sum + " с " + insurance_statement.jsonData.export_contract.price_detals.prev_payment_obligations_info.begin_date + " по " + insurance_statement.jsonData.export_contract.price_detals.prev_payment_obligations_info.end_date
        return prev_payment_obligations_info_other_str
    } else return " "
}

def payment_delay_type_sum(insurance_statement) {
    if (insurance_statement.jsonData.export_contract.payment_delay.payment_delay_type == "at_time") {
        return insurance_statement.jsonData.export_contract.payment_delay.sum + " " + insurance_statement.jsonData.export_contract.payment_delay.sum_currency
    }
    if (insurance_statement.jsonData.export_contract.payment_delay.payment_delay_type == "at_certain_time") {
        return insurance_statement.jsonData.export_contract.payment_delay.sum + " " + insurance_statement.jsonData.export_contract.payment_delay?.sum_currency
    }
}

def payment_delay_percents_info_full_sum(insurance_statement,functionMap){
    if(insurance_statement.jsonData.export_contract.price_include_percents != "no"){
        return Eval.x(insurance_statement.jsonData.export_contract.price_detals.payment_delay_percents_info?.full_sum, functionMap.amountByCategory) + "\n" + Eval.x(insurance_statement.jsonData.export_contract.price_detals.payment_delay_percents_info?.interest_rate, functionMap.amountByCategory) + " %"
    }
    else {
        return null
    }
}

def payment_delay_percents_info_insured_sum(insurance_statement){
    if(insurance_statement.jsonData.export_contract.price_include_percents != "no"){
        return insurance_statement.jsonData.export_contract.price_detals.payment_delay_percents_info?.insured_sum
    }
    else {
        return null
    }
}

def payment_delay_type_at_time_date(insurance_statement) {
    if(insurance_statement.jsonData.export_contract.is_advance != "no") {
        if (insurance_statement.jsonData.export_contract.payment_delay.payment_delay_type == "at_time") {
            return insurance_statement.jsonData.export_contract.advance?.payment_date_or_duration == "1" ? insurance_statement.jsonData.export_contract.advance.payment_date : insurance_statement.jsonData.export_contract.advance.payment_duration + " " + insurance_statement.jsonData.export_contract.advance.payment_duration_range_type.value
        }
        if (insurance_statement.jsonData.export_contract.payment_delay.payment_delay_type == "at_certain_time") {
            return insurance_statement.jsonData.export_contract.advance?.payment_date_or_duration == "1" ? insurance_statement.jsonData.export_contract.advance.payment_date : insurance_statement.jsonData.export_contract.advance.payment_duration + " " + insurance_statement.jsonData.export_contract.advance.payment_duration_range_type.value
        }
    }
}

def payment_date_or_duration(insurance_statement){
    if(insurance_statement.jsonData.export_contract.is_advance != "no") {
        if(insurance_statement.jsonData.export_contract.advance.payment_date_or_duration == "1"){
            return insurance_statement.jsonData.export_contract.advance.payment_date
        }
        else {
            return insurance_statement.jsonData.export_contract.advance.payment_duration + " " + insurance_statement.jsonData.export_contract.advance.payment_duration_range_type.value
        }
    }
}




def akkreditives = []
if(insurance_statement.jsonData.export_contract.akkreditives.size() == 0){
    def akkreditivesMap = [
            "id_number"                         : null,
            "open_date"                         : null,
            "validity_begin_date"               : null,
            "validity_end_date"                 : null,
            "credit_subject"                    : null,
            "all_subject_to_payment"            : checkFalse,
            "specification_enumeration"         : null,
            "sum_and_currency"                  : null,
            "payments_by_delay_conditions_true" : checkFalse,
            "payments_by_delay_conditions_false": checkFalse,
            "payment_delay_duration"            : null
    ]
    akkreditives.add(akkreditivesMap);
}
else {
    insurance_statement.jsonData.export_contract.akkreditives.each {
        def akkreditivesMap = [
                "id_number"                         : it.id_number,
                "open_date"                         : it.open_date,
                "validity_begin_date"               : it.validity_begin_date,
                "validity_end_date"                 : it.validity_end_date,
                "credit_subject"                    : "оплата по Экспортному контракту",
                "all_subject_to_payment"            : it.all_subject_to_payment == "yes" ? "<span>&#9746;</span>" : "<span>&#9744;</span>",
                "specification_enumeration"         : it.specification_enumeration,
                "sum_and_currency"                  : it.sum + " " + it.sum_currency.value,
                "payments_by_delay_conditions_true" : it.payments_by_delay_conditions == "yes" ? "<span>&#9746;</span>" : "<span>&#9744;</span>",
                "payments_by_delay_conditions_false": it.payments_by_delay_conditions == "no" ? "<span>&#9746;</span>" : "<span>&#9744;</span>",
                "payment_delay_duration"            : it.payment_delay_duration + " " + it.payment_delay_duration_range_type.value]

        akkreditives.add(akkreditivesMap)
    }
}



def doc_arrays(insurance_statement, doc_type) {
    def doc_checker = false
    insurance_statement.wdDocuments.each {
        if (doc_checker != true) {
            doc_checker = it.outputName.toString().contains(doc_type)
        }
    }
}


def reportMap = [
        //        блок 1
        "client_org_caption" : clientOrg.caption,
        "client_org_ogrn" : clientOrg.ogrn,
        "client_org_address" : clientAddress(clientOrg),
        "first_contact_fio" : contractInsurance(insurance_statement.jsonData.first_contact),
        "first_contact_tel" : insurance_statement.jsonData.first_contact.tel,
        "first_contact_email" : insurance_statement.jsonData.first_contact.email,
        "second_contact_fio" : contractInsurance(insurance_statement.jsonData.second_contact),
        "second_contact_tel" : insurance_statement.jsonData.second_contact.tel,
        "second_contact_email" : insurance_statement.jsonData.second_contact.email,

        //        блок 2
        "export_type_1"                                             : insurance_statement.jsonData.rus_export_types.contains("export_type_1") ? checkTrue : checkFalse,
        "export_type_2"                                             : insurance_statement.jsonData.rus_export_types.contains("export_type_2") ? checkTrue : checkFalse,
        "export_type_3"                                             : insurance_statement.jsonData.rus_export_types.contains("export_type_3") ? checkTrue : checkFalse,
        "export_type_4"                                             : insurance_statement.jsonData.rus_export_types.contains("export_type_4") ? checkTrue : checkFalse,
        "export_type_5"                                             : insurance_statement.jsonData.rus_export_types.contains("export_type_5") ? checkTrue : checkFalse,
        "export_type_6"                                             : insurance_statement.jsonData.rus_export_types.contains("export_type_6") ? checkTrue : checkFalse,
        "export_type_7"                                             : insurance_statement.jsonData.rus_export_types.contains("export_type_7") ? checkTrue : checkFalse,
        "export_type_8"                                             : insurance_statement.jsonData.rus_export_types.contains("export_type_8") ? checkTrue : checkFalse,
        "other_export_charge"                                       : insurance_statement.jsonData.other_export_charge,
        //        блок 3
        "debtor_type_1"                                             : insurance_statement.jsonData.debtor_info.debtor_type.contains("debtor_type_1") ? checkTrue : checkFalse,
        "debtor_type_2"                                             : insurance_statement.jsonData.debtor_info.debtor_type.contains("debtor_type_2") ? checkTrue : checkFalse,
        "debtor_type_3"                                             : insurance_statement.jsonData.debtor_info.debtor_type.contains("debtor_type_3") ? checkTrue : checkFalse,
        "debtor_type_4"                                             : insurance_statement.jsonData.debtor_info.debtor_type.contains("debtor_type_4") ? checkTrue : checkFalse,

        "debtor_info_fullname"                                      : insurance_statement.jsonData.debtor_info?.fullname,
        "debtor_info_reg_num"                                       : insurance_statement.jsonData.debtor_info.regnum,
        "debtor_info_address"                                       : insurance_statement.jsonData.debtor_info.address + ", " + insurance_statement.jsonData.debtor_info.country?.value,

        "debtor_is_minfin_true"                                     : insurance_statement.jsonData.debtor_is_minfin == "yes" ? checkTrue : checkFalse,
        "debtor_is_minfin_false"                                    : insurance_statement.jsonData.debtor_is_minfin == "no" ? checkTrue : checkFalse,

        "is_minfin_guarantee_true"                                  : insurance_statement.jsonData.is_minfin_guarantee == "yes" ? checkTrue : checkFalse,
        "is_minfin_guarantee_false"                                 : insurance_statement.jsonData.is_minfin_guarantee == null || insurance_statement.jsonData.is_minfin_guarantee == "no" ? checkTrue : checkFalse,

        "debtor_under_insured_control"                              : insurance_statement.jsonData.debtor_under_insured_control == "yes" ? checkTrue : checkFalse,

        "debtor_control_insured"                                    : insurance_statement.jsonData.debtor_control_insured == "yes" ? checkTrue : checkFalse,

        "debtor_and_insured_under_control"                          : insurance_statement.jsonData.debtor_and_insured_under_control == "yes" ? checkTrue : checkFalse,

        "debtor_insured_not_affiliated"                             : insurance_statement.jsonData.debtor_insured_not_affiliated == "yes" ? checkTrue : checkFalse
        ,
        "any_prev_contracts_true"                                   : insurance_statement.jsonData.any_prev_contracts == "yes" ? checkTrue : checkFalse,
        "any_prev_contracts_false"                                  : insurance_statement.jsonData.any_prev_contracts == "no" ? checkTrue : checkFalse,

        "any_prev_contract_deadline_true"                           : insurance_statement.jsonData.any_prev_contract_deadline == "yes" ? checkTrue : checkFalse,
        "any_prev_contract_deadline_false"                          : insurance_statement.jsonData.any_prev_contract_deadline == "no" ? checkTrue : checkFalse,

        //        блок 4 - А
        "export_contract_name"                                      : insurance_statement.jsonData.export_contract.name,
        "export_contract_number"                                    : insurance_statement.jsonData.export_contract.number,
        "export_contract_sign_date"                                 : insurance_statement.jsonData.export_contract.sign_date,

        //        блок 4 - Б
        "buyer_info_fullname"                                       : insurance_statement.jsonData.export_contract.buyer_info?.fullname,
        "buyer_info_reg_num"                                        : insurance_statement.jsonData.export_contract.buyer_info?.regnum,
        "buyer_info_address"                                        : insurance_statement.jsonData.export_contract.buyer_info?.address + "," + insurance_statement.jsonData.export_contract.buyer_info.country?.value,

        //        блок 4 - В  Eval.x(insurance_statement.jsonData.export_contract.price, functionMap.amountByCategory),
        "export_contract_price"                                     : insurance_statement.jsonData.export_contract.price,
        "export_contract_price_currency"                            : insurance_statement.jsonData.export_contract.price_currency.value,

        //        блок 4 - В Российские товары (работы, услуги и (или) права использования РИД): Eval.x(!!!!, functionMap.amountByCategory),
        "rus_items_info_export_subjects"                            : insurance_statement.jsonData.export_contract.price_detals.rus_items_info?.export_subjects,
        "rus_items_info_export_full_sum"                            : insurance_statement.jsonData.export_contract.price_detals.rus_items_info?.full_sum,
        "rus_items_info_insured_sum"                                : insurance_statement.jsonData.export_contract.price_detals.rus_items_info?.insured_sum,

        "attendant_items_info_export_subjects"                      : insurance_statement.jsonData.export_contract.price_detals.attendant_items_info?.export_subjects,
        "attendant_items_info_full_sum"                             : insurance_statement.jsonData.export_contract.price_detals.attendant_items_info?.full_sum,
        "attendant_items_info_insured_sum"                          : insurance_statement.jsonData.export_contract.price_detals.attendant_items_info?.insured_sum,

        "costs_in_debtor_country_export_subjects"                   : insurance_statement.jsonData.export_contract.price_detals.costs_in_debtor_country?.export_subjects,
        "costs_in_debtor_country_full_sum"                          : insurance_statement.jsonData.export_contract.price_detals.costs_in_debtor_country?.full_sum,
        "costs_in_debtor_country_insured_sum"                       : insurance_statement.jsonData.export_contract.price_detals.costs_in_debtor_country.insured_sum,

        "foreign_items_info_export_subjects"                        : insurance_statement.jsonData.export_contract.price_detals.foreign_items_info?.export_subjects,
        "foreign_items_info_full_sum"                               : insurance_statement.jsonData.export_contract.price_detals.foreign_items_info?.full_sum,
        "foreign_items_info_insured_sum"                            : insurance_statement.jsonData.export_contract.price_detals.foreign_items_info?.insured_sum,

        "foreign_part_of_foreign_items_export_subjects"             : insurance_statement.jsonData.export_contract.price_detals.foreign_part_of_foreign_items?.export_subjects,
        "foreign_part_of_foreign_items_full_sum"                    : insurance_statement.jsonData.export_contract.price_detals.foreign_part_of_foreign_items?.full_sum,
        "foreign_part_of_foreign_items_insured_sum"                 : insurance_statement.jsonData.export_contract.price_detals.foreign_part_of_foreign_items?.insured_sum,

        "payment_delay_percents_info_full_sum"                      : payment_delay_percents_info_full_sum(insurance_statement,functionMap),
        "payment_delay_percents_info_insured_sum"                   : Eval.x(payment_delay_percents_info_insured_sum(insurance_statement), functionMap.amountByCategory),

        "compensation_sum_info_full_sum"                            : insurance_statement.jsonData.export_contract.price_detals.compensation_sum_info?.full_sum,
        "compensation_sum_info_insured_sum"                         : insurance_statement.jsonData.export_contract.price_detals.compensation_sum_info?.insured_sum,

        //Иное:
        "other_sum_info_other_obligation"                           : insurance_statement.jsonData.export_contract.price_detals.other_sum_info?.other_obligation,
        "other_sum_info_full_sum"                                   : insurance_statement.jsonData.export_contract.price_detals.other_sum_info?.full_sum,
        "other_sum_info_insured_sum"                                : insurance_statement.jsonData.export_contract.price_detals.other_sum_info?.insured_sum,

        //        блок 4 - Г
        "price_details_rus_content_part"                            : insurance_statement.jsonData.export_contract.price_detals.rus_content_part,

        //        блок 4 - Д
        "price_details_any_prev_payment_obligations_true"           : insurance_statement.jsonData.export_contract.price_detals.any_prev_payment_obligations == "yes" ? checkTrue : checkFalse,
        "price_details_any_prev_payment_obligations_false"          : insurance_statement.jsonData.export_contract.price_detals.any_prev_payment_obligations == "no" ? checkTrue : checkFalse,

        "prev_payment_obligations_info_other"                       : prev_payment_obligations_info_other(insurance_statement),

        //        блок 4 - Е
        "export_contract_execution_one_time_delivery"               : insurance_statement.jsonData.export_contract.export_contract_execution == "one_time_delivery" ? checkTrue : checkFalse,
        "export_contract_execution_delivery_on_schedule"            : insurance_statement.jsonData.export_contract.export_contract_execution == "delivery_on_schedule" ? checkTrue : checkFalse,
        "export_contract_execution_buyer_s_requests"                : insurance_statement.jsonData.export_contract.export_contract_execution == "buyer_s_requests" ? checkTrue : checkFalse,
        "export_contract_execution_other"                           : insurance_statement.jsonData.export_contract.export_contract_execution == "other" ? checkTrue : checkFalse,
        "export_contract_other_export_contract_execution"           : insurance_statement.jsonData.export_contract.other_export_contract_execution,

        //        блок 4 - Ж По Экспортному контракту
        //эк
        "ep_periods_begin"                                          : insurance_statement.jsonData.export_contract.export_periods.begin_value_type == "1" ? insurance_statement.jsonData.export_contract.export_periods.begin_date : insurance_statement.jsonData.export_contract.export_periods.begin_other,
        "ep_end_value_type"                                         : insurance_statement.jsonData.export_contract.export_periods.end_value_type == "1" ? insurance_statement.jsonData.export_contract.export_periods.end_date : insurance_statement.jsonData.export_contract.export_periods.end_other,
        "pp_begin_value_type"                                       : insurance_statement.jsonData.export_contract.payment_periods.begin_value_type == "1" ? insurance_statement.jsonData.export_contract.payment_periods.begin_date : insurance_statement.jsonData.export_contract.payment_periods.begin_other,
        "pp_end_value_type"                                         : insurance_statement.jsonData.export_contract.payment_periods.end_value_type == "1" ? insurance_statement.jsonData.export_contract.payment_periods.end_date : insurance_statement.jsonData.export_contract.payment_periods.end_other,
        //платежи
        "ec_begin_by_payment_obligations_value_data_checker"        : insurance_statement.jsonData.export_contract.begin_by_payment_obligations_value_type == "1" ? checkTrue : checkFalse,
        "ec_begin_by_payment_obligations_other_checker"             : insurance_statement.jsonData.export_contract.begin_by_payment_obligations_value_type == "2" ? checkTrue : checkFalse,
        "ec_begin_by_payment_obligations_value_data"                : insurance_statement.jsonData.export_contract.export_periods.begin_by_payment_obligations_date,

        "ec_end_by_payment_obligations_data"                        : insurance_statement.jsonData.export_contract.end_by_payment_obligations_value_type == "1" ? insurance_statement.jsonData.export_contract.end_by_payment_obligations_date : insurance_statement.jsonData.export_contract.end_by_payment_obligations_other,

        "pp_begin_by_payment_obligations_data_checker"              : insurance_statement.jsonData.export_contract.payment_periods.begin_by_payment_obligations_value_type == "1" ? checkTrue : checkFalse,
        "pp_begin_by_payment_obligations_other_checker"             : insurance_statement.jsonData.export_contract.payment_periods.begin_by_payment_obligations_value_type == "2" ? checkTrue : checkFalse,
        "pp_begin_by_payment_obligations_data"                      : insurance_statement.jsonData.export_contract.payment_periods.begin_by_payment_obligations_date,

        "pp_end_by_payment_obligations_value_type"                  : insurance_statement.jsonData.export_contract.payment_periods.end_by_payment_obligations_value_type == "1" ? insurance_statement.jsonData.export_contract.payment_periods.end_by_payment_obligations_date : insurance_statement.jsonData.export_contract.payment_periods.end_by_payment_obligations_other,

        //        блок 4 - З Порядок исполнения Платежных обязательств
        //аванс
        "export_contract_advance_sum"                               : insurance_statement.jsonData.export_contract.advance?.sum + " " + insurance_statement.jsonData.export_contract.advance?.currency_prepaid_expense?.value,
        "advance_payment_date_or_duration"                         : payment_date_or_duration(insurance_statement),
        //отсрочка

        "payment_delay_payment_delay_type_at_time_sum"              : payment_delay_type_sum(insurance_statement),
        "payment_delay_payment_delay_type_at_time_date"             : payment_delay_type_at_time_date(insurance_statement),


        "payment_delay_payment_delay_type_at_certain_time_sum"      : payment_delay_type_sum(insurance_statement),
        "payment_delay_payment_delay_type_at_certain_time_date"     : payment_delay_type_at_time_date(insurance_statement),

        "payment_delay_payment_delay_type_according_to_the_schedule": insurance_statement.jsonData.export_contract.payment_delay.payment_delay_type == "according_to_the_schedule" ? checkTrue : checkFalse,

        "is_other_payment_order"                                    : insurance_statement.jsonData.export_contract.other_payment_order?.payment_order,


        //        блок 4 - И Специальные действия  в рамках Экспортного контракта
        "other_export_charge_true"                                  : insurance_statement.jsonData.other_export_charge == "yes" ? checkTrue : checkFalse,
        "other_export_charge_false"                                 : insurance_statement.jsonData.other_export_charge == "no" ? checkTrue : checkFalse,
        "other_export_charge_detail"                                : "",

        //        блок 4 - K Прочие условия Экспортного контракта
        "law_data_court_type"                                       : insurance_statement.jsonData.export_contract.law_data.court_type_str,
        "law_data_venue_for_disputes"                               : insurance_statement.jsonData.export_contract.law_data.venue_for_disputes,
        "law_data_ec_applicable_law"                                : insurance_statement.jsonData.export_contract.law_data.ec_applicable_law.value,

        "pledge_is_banned_true"                                     : insurance_statement.jsonData.export_contract.pledge_is_banned == "yes" ? checkTrue : checkFalse,
        "pledge_is_banned_false"                                    : insurance_statement.jsonData.export_contract.pledge_is_banned == "no" ? checkTrue : checkFalse,
        "pledge_ban_description"                                    : insurance_statement.jsonData.export_contract.pledge_ban_description,

        //        блок 4 - Л Прочие условия Экспортного контракта
        "export_contract_other_info"                                : insurance_statement.jsonData.export_contract.other_info,


        //        блок 5 - Л Прочие условия Экспортного контракта
        "akkreditives"                                              : akkreditives,

        //        блок 6 - Выгодоприобретатель
        "is_beneficiary_yes"                                        : insurance_statement.jsonData.is_beneficiary == "yes" ? checkTrue : checkFalse,
        "is_beneficiary_no"                                         : insurance_statement.jsonData.is_beneficiary == "yes" ? checkTrue : checkFalse,
        "beneficiary_info_fullname"                                 : insurance_statement.jsonData.beneficiary_info?.fullname,
        "beneficiary_info_regnum"                                   : insurance_statement.jsonData.beneficiary_info.regnum,
        "beneficiary_info_address"                                  : insurance_statement.jsonData.beneficiary_info?.address,
        "beneficiary_info_contract"                                 : insurance_statement.jsonData.beneficiary_info.contract_num + " от " + insurance_statement.jsonData.beneficiary_info.contract_date,
        "debtor_under_beneficiary_control"                          : insurance_statement.jsonData.beneficiary_info.debtor_under_beneficiary_control == "yes" ? checkTrue : checkFalse,
        "debtor_control_beneficiary"                                : insurance_statement.jsonData.beneficiary_info.debtor_control_beneficiary == "yes" ? checkTrue : checkFalse,
        "debtor_and_beneficiary_under_control"                      : insurance_statement.jsonData.beneficiary_info.debtor_and_beneficiary_under_control == "yes" ? checkTrue : checkFalse,
        "debtor_beneficiary_not_affiliated"                         : insurance_statement.jsonData.beneficiary_info.debtor_beneficiary_not_affiliated == "yes" ? checkTrue : checkFalse,


        //    блок 9 Указать документы (копии), которые предоставляются Страхователем
        "export_contract_all_applications"                          : doc_arrays(insurance_statement, "exportContractDoc") ? checkTrue : checkFalse,
        "letter_credit_opening_documents"                           : doc_arrays(insurance_statement, "exportContractDoc") ? checkTrue : checkFalse,
        "provision_and_document,"                                   : doc_arrays(insurance_statement, "collateralsDoc") ? checkTrue : checkFalse,
        "debtor_financial_statements"                               : doc_arrays(insurance_statement, "otherInfoDoc") ? checkTrue : checkFalse,
        "documents_authority"                                       : doc_arrays(insurance_statement, "confirmingTheRightToSignDoc") ? checkTrue : checkFalse,
        "founding_documents"                                        : doc_arrays(insurance_statement, "я хз") ? checkTrue : checkFalse,
]

def data = ["data_map" : reportMap]

Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .create();
String jsonOutput = gson.toJson(data);

ResourceGroovyMethods.write(new File("data.json"),jsonOutput)

/*println new JsonBuilder(reportMap).toPrettyString()*/

