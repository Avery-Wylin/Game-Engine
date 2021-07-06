package entities;

import java.util.ArrayList;
import org.joml.Vector3f;


public class EntityManager {
    private ArrayList<Entity> domain;
    private float cx=0,cz=0;
    
    public EntityManager(){
        domain = new ArrayList<>();
    }
    
    public void add(Entity e){
        domain.add(e);
    }
    
    public void remove(Entity e){
        domain.remove(e);
    }
    
    public void setVisibleByRadius(float wx, float wz,float rad,float escapePercent){
        
        if(Math.abs(wx-cx)<escapePercent*rad && Math.abs(wz-cz)<escapePercent*rad ){//did not escape radius
            return;
        }
        
        cx=wx;
        cz=wz;
        
        float wx2 = wx+rad;
        float wz2 = wz+rad;
        wx-=rad;
        wz-=rad;
        boolean modX = false;
        boolean modZ = false;
        boolean posX=false;
        boolean posZ=false;
        
        if(wx<0){
            wx%=Terrain.SCALE;
            wx+=Terrain.SCALE;
            modX=true;
        }
        else if(wx2>Terrain.SCALE){
            wx2%=Terrain.SCALE;
            modX=true;
            posX=true;
        }
        
        if(wz<0){
            wz%=Terrain.SCALE;
            wz+=Terrain.SCALE;
            modZ=true;
        }
        else if(wz2>Terrain.SCALE){
            wz2%=Terrain.SCALE;
            modZ=true;
            posZ=true;
        }
        
        
        for(Entity e:domain){
            e.modPos.set(0,0,0);
            //x test
            if(modX){//x is modulated
                if(!(e.pos.x>wx || e.pos.x<wx2)){//not in x bounds
                    e.visible=false;
                    continue;
                }
                if(posX && e.pos.x<wx2){
                    e.modPos.add(Terrain.SCALE,0,0);
                }
                else if(!posX && e.pos.x>wx){
                    e.modPos.add(-Terrain.SCALE,0,0);
                }
            }
            else{//x is not modulated
                if(!(e.pos.x>wx && e.pos.x<wx2)){//not in x bounds
                    e.visible=false;
                    continue;
                }
            }
            
            //z test
             if(modZ){//z is modulated
                if(!(e.pos.z>wz || e.pos.z<wz2)){//not in z bounds
                    e.visible=false;
                    continue;
                }
                if(posZ && e.pos.z<wz2){
                    e.modPos.add(0,0,Terrain.SCALE);
                }
                else if(!posZ && e.pos.z>wz){
                    e.modPos.add(0,0,-Terrain.SCALE);
                }
            }
            else{//z is not modulated
                if(!(e.pos.z>wz && e.pos.z<wz2)){//not in z bounds
                    e.visible=false;
                    continue;
                }
            }
            //passed both test
            e.visible=true;
            e.updateTransform();
        }
    }
    
}
