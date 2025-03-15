package com.sankuai.inf.leaf.boot.segment;

import com.sankuai.inf.leaf.boot.LeafException;
import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.common.Status;
import com.sankuai.inf.leaf.segment.SegmentIDGenImpl;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 生成id用的静态方法类。
 *
 * @author chen
 */
public class LeafSegmentGenerator {
    private static SegmentIDGenImpl idGen;

    public static long getId(String type) {
        Result result = idGen.get(type);
        if (result.getStatus() == Status.SUCCESS) {
            return result.getId();
        }
        throw new LeafException("生成id失败:" + result.getId());
    }

    public static String getStringId(String type) {
        Result result = idGen.get(type);
        if (result.getStatus() == Status.SUCCESS) {
            return result.getIdString();
        }
        throw new LeafException("生成id失败:" + result.getId());
    }

    public static String getStringIdWithType(String type) {
        Result result = idGen.get(type);
        if (result.getStatus() == Status.SUCCESS) {
            return type + result.getIdString();
        }
        throw new LeafException("生成id失败:" + result.getId());
    }

    public static String getStringIdWithTypeAndDate(String type) {
        Result result = idGen.get(type);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (result.getStatus() == Status.SUCCESS) {
            return type + format.format(new Date()) + result.getIdString();
        }
        throw new LeafException("生成id失败:" + result.getId());
    }

    public static SegmentIDGenImpl getIdGen() {
        return idGen;
    }

    public static void setIdGen(SegmentIDGenImpl idGen) {
        LeafSegmentGenerator.idGen = idGen;
    }
}
