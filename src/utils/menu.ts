import { h } from "vue";
import { NIcon } from "naive-ui";
import type { MenuOption, MenuDividerOption, MenuGroupOption } from "naive-ui";

/**
 * 动态渲染图标
 * @param icon 图标名称
 */
async function asyncRenderIcon(icon: string) {
  // @ts-ignore
  const { [icon]: iconComp } = await import("@vicons/ionicons5");
  return () => h(NIcon, null, { default: () => h(iconComp) });
}

/**
 * 获取菜单选项
 * @param menu 菜单项
 * @returns 菜单选项
 */
const getOption = async (menu: Menu) => {
  if (menu.label === "divider") {
    return {
      type: "divider",
      // generate a random key to avoid warning
      key: Math.random().toString(36).slice(2),
    };
  }
  if (menu.icon)
    return {
      label: menu.label,
      key: menu.key,
      icon: await asyncRenderIcon(menu.icon),
    };
  return {
    label: menu.label,
    key: menu.key,
  };
};
/**
 * 渲染菜单
 */
const renderMenu = async (menu: Menu[] | string) => {
  if (typeof menu === "string") menu = JSON.parse(menu) as Menu[];
  let result: Array<MenuOption | MenuDividerOption | MenuGroupOption> = [];
  result = await Promise.all(
    menu.map(async (item) => {
      if (item.children) {
        return {
          label: item.label,
          key: item.key,
          icon: await asyncRenderIcon(item.icon as string),
          children: await Promise.all(
            item.children.map(async (child) => {
              return await getOption(child);
            })
          ),
        };
      } else {
        return await getOption(item);
      }
    })
  );
  return result;
};

export default renderMenu;