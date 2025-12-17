declare module 'pixi-live2d-display' {
  import * as PIXI from 'pixi.js'

  export class Live2DModel extends PIXI.Container {
    static from(source: string, options?: Live2DModelOptions): Promise<Live2DModel>
    
    internalModel: {
      motionManager: any
      settings: any
    }
    
    anchor: PIXI.ObservablePoint
    
    expression(name: string | number): Promise<any>
    motion(group: string, index?: number, priority?: number): Promise<any>
    focus(x: number, y: number): void
    tap(x: number, y: number): void
    
    on(event: 'hit', fn: (hitAreas: string[]) => void): this
  }

  export interface Live2DModelOptions {
    autoInteract?: boolean
    autoUpdate?: boolean
  }
}
