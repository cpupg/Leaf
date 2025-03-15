package com.sankuai.inf.leaf.segment.dao;

import com.sankuai.inf.leaf.segment.model.LeafAlloc;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 操作号段表用到的sql。
 *
 * <p>可以是mapper，也可以是service，总之能操作号段表就行。你可以基于自己的业务添加更多sql，
 * 但是此接口种的sql必须有。</p>
 */
public interface IDAllocDao {
    /**
     * 获取号段表中的所有业务号段。
     *
     * @return 业务号段。
     */
    List<LeafAlloc> getAllLeafAllocs();

    /**
     * 更新号段表兵返回最新数据。
     *
     * @param tag 业务类型。
     *
     * @return 最新号段。
     */
    LeafAlloc updateMaxIdAndGetLeafAlloc(String tag);

    /**
     * 更新步长并返回最新号段。
     *
     * @param leafAlloc 步长。
     *
     * @return 号段。
     */
    LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc);

    /**
     * 返回所有号段标签。
     *
     * @return 数据库中保存的所有号段标签。
     */
    List<String> getAllTags();

    /**
     * 重置id。
     *
     * @param key 业务类习惯。
     * @param step 步长。
     *
     * @return 重置后的id。
     */
    LeafAlloc resetLeafAlloc(@Param("key") String key, @Param("step") int step);
}
