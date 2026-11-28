package edu.hitsz.rank;

public interface RankDao {
    /**
     * 保存一条排名数据。
     *
     * @param mode 排行榜模式标识
     * @param rank 待保存的排名记录
     */
    void saveRank(String mode, Rank rank);

    /**
     * 获取前 n 名排名数据。
     *
     * @param mode 排行榜模式标识
     * @param n 需要返回的记录条数
     * @return 按规则排序后的前 n 名记录
     */
    Rank[] getTopRanks(String mode, int n);

    /**
     * 获取所有排名数据（已排序并带名次）。
     */
    Rank[] getAllRanks(String mode);

    /**
     * 删除一条排名记录。
     *
     * @param mode 排行榜模式标识
     * @param rank 待删除记录
     */
    void deleteRank(String mode, Rank rank);
}
