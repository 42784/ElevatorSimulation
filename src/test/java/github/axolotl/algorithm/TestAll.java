package github.axolotl.algorithm;

import org.junit.jupiter.api.Test;

/**
 * @author AxolotlXM
 * @Created by Axolotl
 * @Date 2025/4/13 16:22
 */
public class TestAll {
    @Test
    public void testFCFS() {
        github.axolotl.model.TestModel.modelA(new Algorithm_FCFS(), "modelA-FCFS");
        github.axolotl.model.TestModel.modelB(new Algorithm_FCFS(), "modelB-FCFS");
    }

    @Test
    public void testLOOK() {
        github.axolotl.model.TestModel.modelA(new Algorithm_LOOK(), "modelA-LOOK");
        github.axolotl.model.TestModel.modelB(new Algorithm_LOOK(), "modelB-LOOK");
    }

    @Test
    public void testFDSCAN() {
        github.axolotl.model.TestModel.modelA(new Algorithm_FDSCAN(), "modelA-FDSCAN");
        github.axolotl.model.TestModel.modelB(new Algorithm_FDSCAN(), "modelB-FDSCAN");
    }

    @Test
    public void testSSTF() {
        github.axolotl.model.TestModel.modelA(new Algorithm_SSTF(), "modelA-SSTF");
        github.axolotl.model.TestModel.modelB(new Algorithm_SSTF(), "modelB-SSTF");
    }

    @Test
    public void testSCAN() {
        github.axolotl.model.TestModel.modelA(new Algorithm_SCAN(), "modelA-SCAN");
        github.axolotl.model.TestModel.modelB(new Algorithm_SCAN(), "modelB-SCAN");
    }
}
