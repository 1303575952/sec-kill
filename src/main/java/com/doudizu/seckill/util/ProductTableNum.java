package com.doudizu.seckill.util;

import java.math.BigInteger;

public class ProductTableNum {
    public static long getProductTableNum(long pid) {
        if (pid < 133808073L || pid > 3163885158L) {
            throw new RuntimeException();
        }
        long[] arr = {184325646L,
                234837087L,
                285252483L,
                335735448L,
                386272327L,
                436739072L,
                487223988L,
                537668517L,
                588209890L,
                638725397L,
                689240949L,
                739752144L,
                790269926L,
                840754738L,
                891221579L,
                941703934L,
                992221345L,
                1042712541L,
                1093169837L,
                1143725245L,
                1194195487L,
                1244706772L,
                1295235145L,
                1345670268L,
                1396178541L,
                1446659588L,
                1497157748L,
                1547683499L,
                1598149441L,
                1648688502L,
                1699210216L,
                1749675001L,
                1800240927L,
                1850724005L,
                1901243052L,
                1951771341L,
                2002239488L,
                2052819182L,
                2103325720L,
                2153837738L,
                2204362424L,
                2254897982L,
                2305434306L,
                2355965313L,
                2406418900L,
                2456936953L,
                2507453331L,
                2557923329L,
                2608369479L,
                2658853547L,
                2709348891L,
                2759903331L,
                2810401116L,
                2860937346L,
                2911415733L,
                2961943152L,
                3012433841L,
                3062923969L,
                3113386710L,
                3163885158L};
        int i = 0;
        for (; i < arr.length; i++) {
            if (pid > arr[i]) continue;
            //return i;
            break;
        }
        return i;
    }

    public static void main(String[] args) {
        System.out.println(getProductTableNum(3113386734L));
    }
}
