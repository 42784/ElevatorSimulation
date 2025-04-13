package github.axolotl.algorithm;

import org.junit.jupiter.api.Test;

/**
 * @author AxolotlXM
 * @Created by Axolotl
 * @Date 2025/4/13 16:22
 */
public class TestAll {
    @Test
    public void testAll() {
        github.axolotl.model.TestModel.modelA(new Algorithm_FCFS(),"modelA-FDSC");
        github.axolotl.model.TestModel.modelB(new Algorithm_FCFS(),"modelB-FDSC");

        github.axolotl.model.TestModel.modelA(new Algorithm_LOOK(),"modelA-LOOK");
        github.axolotl.model.TestModel.modelB(new Algorithm_LOOK(),"modelB-LOOK");

        github.axolotl.model.TestModel.modelA(new Algorithm_FDSCAN(),"modelA-FDSCAN");
        github.axolotl.model.TestModel.modelB(new Algorithm_FDSCAN(),"modelB-FDSCAN");

        github.axolotl.model.TestModel.modelA(new Algorithm_SSTF(),"modelA-SSTF");
        github.axolotl.model.TestModel.modelB(new Algorithm_SSTF(),"modelB-SSTF");

        github.axolotl.model.TestModel.modelA(new Algorithm_SCAN(),"modelA-SCAN");
        github.axolotl.model.TestModel.modelB(new Algorithm_SCAN(),"modelB-SCAN");

//        github.axolotl.model.TestModel.modelC(new Algorithm_FCFS(),"modelC-FDSCAN");
//        github.axolotl.model.TestModel.modelD(new Algorithm_FCFS(),"modelD-FDSCAN");
    }
}
