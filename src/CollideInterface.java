/**
 * 碰撞方法接口
 */
public interface CollideInterface {
    /**
     * 判断小球是否碰撞函数
     *
     * @param object_x      被判断物体的x轴起始坐标
     * @param object_y      被判断物体的y轴起始坐标
     * @param object_height 被判断物体的高度
     * @param object_width  被判断物体的宽度
     * @return 返回true, 则表示发生了碰撞, 否则无碰撞
     */
    boolean collide(int object_x, int object_y, int object_width, int object_height);

}
