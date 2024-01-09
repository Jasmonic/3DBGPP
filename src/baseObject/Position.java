package baseObject;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-12-2022-12-12
 * @Description: BPP_Model
 * @version=1.0
 */
public class Position {
    public int type;
    public int id;
    public int x;//坐标
    public int y;
    public int z;
    public int lx;//总长度
    public int ly;
    public int lz;
    public double score;

    public Position(int type, int x, int y, int z, int lx, int ly, int lz) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.lx = lx;
        this.ly = ly;
        this.lz = lz;
    }

    public Position(int type, int id, int x, int y, int z, int lx, int ly, int lz) {
        this.type = type;
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.lx = lx;
        this.ly = ly;
        this.lz = lz;
    }

    public int position(int i) {
        if (i == 0) {
            return this.x;
        } else if (i == 1) {
            return this.y;
        } else {
            return this.z;
        }
    }


    public static boolean canTakeProjection(Position newPosition, Position oldPosition, int direction) {
        //return True if newPosition can take Projection along  direction
        //0 1 2 3 4 5 --> Yx Yz Xy Xz Zx Zy
        //
        boolean canProj = false;
        int x, y, z;//圆点坐标
        if (direction == 0 || direction == 1) {
            x = newPosition.getX();
            y = newPosition.getY() + newPosition.getLy();
            z = newPosition.getZ();
        } else {
            if (direction == 2 || direction == 3) {
                x = newPosition.getX() + newPosition.getLx();
                y = newPosition.getY();
                z = newPosition.getZ();
            } else {
                x = newPosition.getX();
                y = newPosition.getY();
                z = newPosition.getZ() + newPosition.getLz();
            }
        }
        //沿着x轴投射到oldPosition的YZ侧面，看看是否在里面
        if (direction == 0 || direction == 4) {
            if (x >= oldPosition.getX() + oldPosition.getLx() &&
                    oldPosition.getY() <= y && y <= oldPosition.getY() + oldPosition.getLy() && oldPosition.getZ() <= z && z <= oldPosition.getZ() + oldPosition.getLz())
                canProj = true;
        } else {
            if (direction == 1 || direction == 3) {//投射到XY
                if (z >= oldPosition.getZ() + oldPosition.getLz() &&
                        oldPosition.getY() <= y && y <= oldPosition.getY() + oldPosition.getLy() && oldPosition.getX() <= x && x <= oldPosition.getX() + oldPosition.getLx())
                    canProj = true;
            } else {//投射到XZ
                if (y >= oldPosition.getY() + oldPosition.getLy() &&
                        oldPosition.getX() <= x && x <= oldPosition.getX() + oldPosition.getLx() && oldPosition.getZ() <= z && z <= oldPosition.getZ() + oldPosition.getLz())
                    canProj = true;
            }
        }
        return canProj;
    }

    @Override
    public String toString() {
        return "Position{" +
                "type=" + type +
                ", id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", lx=" + lx +
                ", ly=" + ly +
                ", lz=" + lz +
                '}';
    }

    public Position copy() {
        return new Position(
                this.getType(),
                this.getId(),
                this.getX(),
                this.getY(),
                this.getZ(),
                this.getLx(),
                this.getLy(),
                this.getLz()
        );
    }

    public int getLx() {
        return lx;
    }

    public int getLy() {
        return ly;
    }

    public int getLz() {
        return lz;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setLx(int lx) {
        this.lx = lx;
    }

    public void setLy(int ly) {
        this.ly = ly;
    }

    public void setLz(int lz) {
        this.lz = lz;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double  score) {
        this.score = score;
    }
}
