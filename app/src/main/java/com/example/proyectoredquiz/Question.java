package com.example.proyectoredquiz;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Question {
    private String pregunta;
    private String categoria;

    public Question() {
    }

    public Question(String pregunta, String categoria) {
        this.pregunta = pregunta;
        this.categoria = categoria;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
