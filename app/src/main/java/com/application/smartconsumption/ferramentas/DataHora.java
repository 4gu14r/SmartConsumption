package com.application.smartconsumption.ferramentas;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DataHora {
    private Context context;
    private int[] dataNascimentoNum;
    public String dataNascString;

    public DataHora(Context context) {
        this.context = context;
    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
    public int[] getDataNascimentoNum() {
        return dataNascimentoNum;
    }
    public void setDataNascimentoNum(int[] dataNascimentoNum) {
        this.dataNascimentoNum = dataNascimentoNum;
    }
    public String getDataNascString() {
        return dataNascString;
    }
    public void setDataNascString(String dataNascString) {
        this.dataNascString = dataNascString;
    }


    public DatePickerDialog calendario(){ //função para aparecer o calendario
        DatePickerDialog dataDiag = new DatePickerDialog(context,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {}}, obterData()[2], obterData()[1] - 1, obterData()[0]);
        dataDiag.getDatePicker().setMaxDate(System.currentTimeMillis());
        return dataDiag;
    }
    public int[] converterDataStrToInt(String[] dataStr) {
        if (dataStr == null || dataStr.length != 3) {
            // Trate o caso em que o array de strings não possui o formato esperado
            return new int[]{0, 0, 0}; // Ou outra lógica apropriada
        }

        int[] result = new int[3];
        for (int i = 0; i < 3; i++) {
            if (dataStr[i].isEmpty()) {
                // Trate o caso em que uma string está vazia
                result[i] = 0; // Ou outra lógica apropriada
            } else {
                try {
                    result[i] = Integer.parseInt(dataStr[i]);
                } catch (NumberFormatException e) {
                    // Trate o caso em que a conversão falha
                    result[i] = 0; // Ou outra lógica apropriada
                }
            }
        }

        return result;
    }
    public Date dataNascimento(){
        String dtnas = this.getDataNascString() +" 03:00:00";

        SimpleDateFormat template = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Calendar data = Calendar.getInstance();

        try {
            data.setTime(template.parse(dtnas));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return data.getTime();
    }
    public int[] obterData(){ // função para obter a data de hoje
        Locale local = new Locale("pt", "BR");

        SimpleDateFormat dia = new SimpleDateFormat("dd", local);
        SimpleDateFormat mes = new SimpleDateFormat("MM", local);
        SimpleDateFormat ano = new SimpleDateFormat("yyyy", local);

        int diaAtual = Integer.parseInt(dia.format(System.currentTimeMillis()));
        int mesAtual = Integer.parseInt(mes.format(System.currentTimeMillis()));
        int anoAtual = Integer.parseInt(ano.format(System.currentTimeMillis()));

        return new int[] {diaAtual,mesAtual,anoAtual};
    }

}
