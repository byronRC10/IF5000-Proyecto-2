/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

import Entity.Archivo;
import java.util.Comparator;

/**
 *
 * @author Fabricio
 */
public class OrdenarArray implements Comparator<Archivo> {

    @Override
    public int compare(Archivo o2, Archivo o1) {
        if (o1.getParte()> o2.getParte()) {
            return -1;
        } else if (o1.getParte() > o2.getParte()) {
            return 0;
        } else {
            return 1;
        }
    }//compare

}// end class
