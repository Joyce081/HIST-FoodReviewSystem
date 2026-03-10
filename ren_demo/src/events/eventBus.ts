import mitt from 'mitt';

// 定义事件类型
type Events = {
  'menu-set-active': { path: string };
};

const eventBus = mitt<Events>();

export default eventBus;
