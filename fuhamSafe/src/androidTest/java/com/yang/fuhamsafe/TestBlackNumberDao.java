package com.yang.fuhamsafe;

import android.test.AndroidTestCase;


import com.yang.fuhamsafe.bean.BlackNumber;
import com.yang.fuhamsafe.dao.BlackNumberDao;

import java.util.Random;

/**
 * Created by fuhamyang on 2015/12/13.
 */
public class TestBlackNumberDao extends AndroidTestCase {
    private BlackNumberDao blackNumberDao;
    @Override
    protected void setUp() throws Exception {
        this.blackNumberDao= new BlackNumberDao(getContext());
        super.setUp();
    }

    public void testInsert(){
        BlackNumber blackNumber;
        Random random = new Random();
        for(long i = 0 ; i < 100 ; i++){
            blackNumber = new BlackNumber((13207751400l+i)+"",(random.nextInt(3)+1)+"");
            blackNumberDao.insertBlackNumber(blackNumber);
        }
    }

    public void testDelete(){
        blackNumberDao.deleteBlackNumber("13207751400");
    }

    public void testFindByNumber(){
        BlackNumber blackNumber = blackNumberDao.selectByBlackNumebr("13207751475");
        System.out.println(blackNumber.getNumber()+"###"+blackNumber.getType());
    }

}
