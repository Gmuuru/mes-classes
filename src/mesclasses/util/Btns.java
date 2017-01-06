/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.util;

import javafx.scene.control.Button;

/**
 *
 * @author rrrt3491
 */
public class Btns {
    
    private static void setHeight(Button btn){
        btn.setMinHeight(30);
        btn.setPrefHeight(30);
        btn.setMaxHeight(30);
    }
    
    public static Button addBtn(){
        return makeDelete(new Button());
    }
    
    public static Button makeAdd(Button btn){
        setHeight(btn);
        btn.setText("\uf067");
        CssUtil.addClass(btn, "button-arrow");
        return btn;
    }
    
    public static Button deleteBtn(){
        return makeDelete(new Button());
    }
    
    public static Button makeDelete(Button btn){
        setHeight(btn);
        btn.setText("\uf00d");
        CssUtil.addClass(btn, "button-delete");
        return btn;
    }
    
    public static Button actionBtn(){
        return makeDelete(new Button());
    }
    
    public static Button makeAction(Button btn){
        setHeight(btn);
        CssUtil.addClass(btn, "button-action");
        return btn;
    }
    
    public static Button upBtn(){
        return makeUp(new Button());
    }
    
    public static Button makeUp(Button btn){
        setHeight(btn);
        btn.setText("\uf077");
        return btn;
    }
    
    public static Button downBtn(){
        return makeDown(new Button());
    }
    
    public static Button makeDown(Button btn){
        setHeight(btn);
        btn.setText("\uf078");
        return btn;
    }
    
    public static Button punitionBtn(){
        return makePunition(new Button());
    }
    
    public static Button makePunition(Button btn){
        setHeight(btn);
        btn.setText("\uf0f6");
        CssUtil.addClass(btn, "warning");
        return btn;
    }
    
    public static Button rapportBtn(){
        return makeRapport(new Button());
    }
    
    public static Button makeRapport(Button btn){
        setHeight(btn);
        btn.setText("\uf03a");
        CssUtil.addClass(btn, "button-arrow");
        return btn;
    }
    
    public static Button leftBtn(){
        return makeLeft(new Button());
    }
    
    public static Button makeLeft(Button btn) {
        btn.setText("\uf053");
        CssUtil.addClass(btn, "button-left");
        setHeight(btn);
        return btn;
    }
    
    public static Button rightBtn(){
        return makeRight(new Button());
    }
    
    public static Button makeRight(Button btn) {
        btn.setText("\uf054");
        CssUtil.addClass(btn, "button-right");
        setHeight(btn);
        return btn;
    }
    
    public static Button arrowUpBtn(){
        return makeArrowUp(new Button());
    }
    
    public static Button makeArrowUp(Button btn) {
        btn.setText("\uf0aa");
        CssUtil.addClass(btn, "button-arrow");
        setHeight(btn);
        return btn;
    }
    
    public static Button arrowDownBtn(){
        return makeArrowDown(new Button());
    }
    
    public static Button makeArrowDown(Button btn) {
        btn.setText("\uf0ab");
        CssUtil.addClass(btn, "button-arrow");
        setHeight(btn);
        return btn;
    }

}
