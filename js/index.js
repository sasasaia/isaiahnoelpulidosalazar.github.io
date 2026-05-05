document.querySelectorAll('.tab-btn').forEach(function(btn) {
  btn.addEventListener('click', function() {
    document.querySelectorAll('.tab-btn').forEach(function(b) { 
      b.classList.remove('background-var(--surface)', 'color-var(--text)', 'borderColor-var(--border)', 'fontWeight-500'); 
      b.classList.add('background-transparent', 'color-var(--muted)');
    });
    document.querySelectorAll('.view').forEach(function(v) { 
      v.classList.remove('display-block', 'animation-fadeIn_0.2s_ease'); 
      v.classList.add('display-none');
    });
    btn.classList.remove('background-transparent', 'color-var(--muted)');
    btn.classList.add('background-var(--surface)', 'color-var(--text)', 'borderColor-var(--border)', 'fontWeight-500');
    var view = document.getElementById('view-' + btn.dataset.view);
    view.classList.remove('display-none');
    view.classList.add('display-block', 'animation-fadeIn_0.2s_ease');
  });
});
document.querySelector(".view-more-projects").addEventListener("click", function() {
  document.querySelectorAll('.tab-btn').forEach(function(b) { 
    b.classList.remove('background-var(--surface)', 'color-var(--text)', 'borderColor-var(--border)', 'fontWeight-500'); 
    b.classList.add('background-transparent', 'color-var(--muted)');
  });
  document.querySelectorAll('.view').forEach(function(v) { 
    v.classList.remove('display-block', 'animation-fadeIn_0.2s_ease'); 
    v.classList.add('display-none');
  });
  var btn = document.querySelector(".tab-btn[data-view='projects']");
  btn.classList.remove('background-transparent', 'color-var(--muted)');
  btn.classList.add('background-var(--surface)', 'color-var(--text)', 'borderColor-var(--border)', 'fontWeight-500');
  var view = document.getElementById('view-projects');
  view.classList.remove('display-none');
  view.classList.add('display-block', 'animation-fadeIn_0.2s_ease');
});