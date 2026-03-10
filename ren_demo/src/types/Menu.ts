export interface Menu {
	index: string;//路由地址
	name: string;//菜单名称
	icon: string;//图标
	children?: Menu[];//子菜单
}
